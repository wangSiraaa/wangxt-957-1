package com.hospital.accompany.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.AuditDTO;
import com.hospital.accompany.entity.*;
import com.hospital.accompany.mapper.*;
import com.hospital.accompany.service.AuditService;
import com.hospital.accompany.vo.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditServiceImpl implements AuditService {

    @Autowired
    private AccompanyApplyMapper applyMapper;

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WardMapper wardMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApplyVO auditApply(AuditDTO dto) {
        AccompanyApply apply = applyMapper.selectById(dto.getApplyId());
        if (apply == null) {
            throw new BusinessException("申请记录不存在");
        }
        if (apply.getApplyStatus() != 0) {
            throw new BusinessException("该申请已审核，不能重复审核");
        }

        apply.setApplyStatus(dto.getAuditResult() == 1 ? 1 : 2);
        apply.setAuditUserId(dto.getAuditUserId());
        apply.setAuditUserName(dto.getAuditUserName());
        apply.setAuditTime(LocalDateTime.now());
        apply.setAuditRemark(dto.getAuditRemark());
        applyMapper.updateById(apply);

        if (dto.getAuditResult() == 1) {
            Patient patient = patientMapper.selectById(apply.getPatientId());
            Ward ward = wardMapper.selectById(apply.getWardId());

            if (ward.getIsIsolation() == 1) {
                throw new BusinessException("隔离病区不能新增陪护");
            }

            Integer validCount = certificateMapper.countValidByPatientId(apply.getPatientId());
            if (validCount >= ward.getMaxAccompanyPerBed()) {
                throw new BusinessException("陪护人数已超过床位限制");
            }

            AccompanyCertificate cert = new AccompanyCertificate();
            String certNo = "PHZ" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                    String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
            cert.setCertNo(certNo);
            cert.setApplyId(apply.getId());
            cert.setPatientId(apply.getPatientId());
            cert.setWardId(apply.getWardId());
            cert.setPersonId(apply.getPersonId());
            cert.setStartDate(apply.getExpectedStartDate());
            cert.setEndDate(apply.getExpectedEndDate());
            cert.setCertStatus(1);
            cert.setIssueUserId(dto.getAuditUserId());
            cert.setIssueUserName(dto.getAuditUserName());
            cert.setIssueTime(LocalDateTime.now());
            certificateMapper.insert(cert);
        }

        return getApplyDetail(apply.getId());
    }

    @Override
    public PageResult<ApplyVO> getPendingList(Long current, Long size, Long wardId,
                                               String patientName, String personName) {
        Page<ApplyVO> page = new Page<>(current, size);
        IPage<ApplyVO> result = applyMapper.selectApplyPage(page, wardId, 0, patientName, personName);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }

    private ApplyVO getApplyDetail(Long id) {
        return applyMapper.getApplyDetailById(id);
    }
}
