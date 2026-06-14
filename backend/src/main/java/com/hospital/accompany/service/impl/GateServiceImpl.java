package com.hospital.accompany.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.GateCheckDTO;
import com.hospital.accompany.entity.*;
import com.hospital.accompany.mapper.*;
import com.hospital.accompany.service.GateService;
import com.hospital.accompany.vo.CertificateVO;
import com.hospital.accompany.vo.GateRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class GateServiceImpl implements GateService {

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Autowired
    private AccompanyPersonMapper personMapper;

    @Autowired
    private WardMapper wardMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private GateRecordMapper gateRecordMapper;

    @Autowired
    private CertLeaveRecordMapper leaveRecordMapper;

    @Override
    public CertificateVO checkCertificate(String certNoOrIdCard) {
        AccompanyCertificate cert = null;

        LambdaQueryWrapper<AccompanyCertificate> certWrapper = new LambdaQueryWrapper<>();
        certWrapper.eq(AccompanyCertificate::getCertNo, certNoOrIdCard);
        cert = certificateMapper.selectOne(certWrapper);

        if (cert == null) {
            LambdaQueryWrapper<AccompanyPerson> personWrapper = new LambdaQueryWrapper<>();
            personWrapper.eq(AccompanyPerson::getIdCard, certNoOrIdCard);
            AccompanyPerson person = personMapper.selectOne(personWrapper);
            if (person != null) {
                LambdaQueryWrapper<AccompanyCertificate> certByIdCardWrapper = new LambdaQueryWrapper<>();
                certByIdCardWrapper.eq(AccompanyCertificate::getPersonId, person.getId())
                        .in(AccompanyCertificate::getCertStatus, 1, 2)
                        .orderByDesc(AccompanyCertificate::getIssueTime)
                        .last("LIMIT 1");
                cert = certificateMapper.selectOne(certByIdCardWrapper);
            }
        }

        if (cert == null) {
            throw new BusinessException("未找到有效的陪护证");
        }

        return getCertificateDetail(cert.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GateRecordVO gateCheck(GateCheckDTO dto) {
        String recordNo = "MJ" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);

        GateRecord record = new GateRecord();
        record.setRecordNo(recordNo);
        record.setGateType(dto.getGateType());
        record.setGateUserId(dto.getGateUserId());
        record.setGateUserName(dto.getGateUserName());
        record.setCheckTime(LocalDateTime.now());

        CertificateVO certVO = null;

        try {
            certVO = checkCertificate(dto.getCertNoOrIdCard());

            if (certVO.getCertStatus() == 0) {
                throw new BusinessException("陪护证已失效");
            }

            if (certVO.getCertStatus() == 2) {
                throw new BusinessException("陪护证处于临时离院状态，请先办理返回手续");
            }

            LocalDate today = LocalDate.now();
            if (today.isBefore(certVO.getStartDate())) {
                throw new BusinessException("陪护证尚未生效");
            }
            if (today.isAfter(certVO.getEndDate())) {
                throw new BusinessException("陪护证已过期");
            }

            if (certVO.getIdCardExpireDate() != null &&
                    certVO.getIdCardExpireDate().isBefore(today)) {
                throw new BusinessException("身份证已过期，不能入院");
            }

            Patient patient = patientMapper.selectById(certVO.getPatientId());
            if (patient != null && !patient.getWardId().equals(certVO.getWardId())) {
                throw new BusinessException("患者已转至其他病区，此陪护证不能在本病区使用");
            }

            Ward ward = wardMapper.selectById(certVO.getWardId());
            if (ward != null && ward.getIsIsolation() == 1) {
                if (certVO.getCertType() == null || certVO.getCertType() != 2) {
                    throw new BusinessException("隔离病区陪护人员禁止出入，需特殊审批陪护证");
                }
            }

            record.setCertId(certVO.getId());
            record.setCertNo(certVO.getCertNo());
            record.setPersonId(certVO.getPersonId());
            record.setPersonName(certVO.getPersonName());
            record.setIdCard(certVO.getIdCard());
            record.setWardId(certVO.getWardId());
            record.setWardName(certVO.getWardName());
            record.setCheckResult(1);

        } catch (BusinessException e) {
            record.setCheckResult(2);
            record.setRefuseReason(e.getMessage());
            record.setPersonName("未知");
            record.setIdCard(dto.getCertNoOrIdCard().length() > 18 ? dto.getCertNoOrIdCard().substring(0, 18) : dto.getCertNoOrIdCard());
            record.setWardId(-1L);
            record.setWardName("未知");
            record.setCertId(-1L);
            record.setCertNo(dto.getCertNoOrIdCard().length() > 32 ? dto.getCertNoOrIdCard().substring(0, 32) : dto.getCertNoOrIdCard());
            record.setPersonId(-1L);
        }

        gateRecordMapper.insert(record);

        GateRecordVO vo = new GateRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setCertInfo(certVO);
        return vo;
    }

    @Override
    public PageResult<GateRecordVO> getRecordPage(Long current, Long size, Long wardId,
                                                   Integer gateType, Integer checkResult,
                                                   String personName, String startDate, String endDate) {
        Page<GateRecordVO> page = new Page<>(current, size);
        IPage<GateRecordVO> result = gateRecordMapper.selectRecordPage(page, wardId, gateType,
                checkResult, personName, startDate, endDate);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }

    private CertificateVO getCertificateDetail(Long certId) {
        return certificateMapper.getCertificateDetailById(certId);
    }
}
