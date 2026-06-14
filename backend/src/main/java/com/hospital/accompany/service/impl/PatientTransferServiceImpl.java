package com.hospital.accompany.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.PatientTransferWardDTO;
import com.hospital.accompany.entity.*;
import com.hospital.accompany.mapper.*;
import com.hospital.accompany.service.PatientTransferService;
import com.hospital.accompany.vo.PatientTransferRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientTransferServiceImpl implements PatientTransferService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WardMapper wardMapper;

    @Autowired
    private PatientTransferRecordMapper transferRecordMapper;

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Autowired
    private AccompanyApplyMapper applyMapper;

    @Autowired
    private WardPatientConfigMapper configMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PatientTransferRecordVO transferWard(PatientTransferWardDTO dto) {
        Patient patient = patientMapper.selectById(dto.getPatientId());
        if (patient == null || patient.getStatus() != 1) {
            throw new BusinessException("患者不存在或已出院");
        }

        Ward fromWard = wardMapper.selectById(patient.getWardId());
        Ward toWard = wardMapper.selectById(dto.getToWardId());
        if (toWard == null || toWard.getStatus() != 1) {
            throw new BusinessException("目标病区不存在或已停用");
        }

        if (patient.getWardId().equals(dto.getToWardId())) {
            throw new BusinessException("目标病区与当前病区相同");
        }

        String fromBedNo = patient.getBedNo();
        String fromWardName = fromWard != null ? fromWard.getWardName() : "";

        LambdaQueryWrapper<AccompanyCertificate> certWrapper = new LambdaQueryWrapper<>();
        certWrapper.eq(AccompanyCertificate::getPatientId, patient.getId())
                .eq(AccompanyCertificate::getCertStatus, 1);
        List<AccompanyCertificate> validCerts = certificateMapper.selectList(certWrapper);

        for (AccompanyCertificate cert : validCerts) {
            cert.setCertStatus(0);
            cert.setInvalidReason("患者转病区，原陪护证自动失效");
            cert.setInvalidTime(LocalDateTime.now());
            cert.setInvalidOperatorId(dto.getOperatorId());
            cert.setInvalidOperatorName(dto.getOperatorName());
            certificateMapper.updateById(cert);

            if (cert.getApplyId() != null) {
                AccompanyApply apply = applyMapper.selectById(cert.getApplyId());
                if (apply != null && apply.getApplyStatus() != 3) {
                    apply.setApplyStatus(3);
                    apply.setInvalidReason("患者转病区，申请自动失效");
                    apply.setInvalidTime(LocalDateTime.now());
                    applyMapper.updateById(apply);
                }
            }
        }

        PatientTransferRecord record = new PatientTransferRecord();
        String transferNo = "ZBQ" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
        record.setTransferNo(transferNo);
        record.setPatientId(patient.getId());
        record.setPatientName(patient.getPatientName());
        record.setFromWardId(patient.getWardId());
        record.setFromWardName(fromWardName);
        record.setFromBedNo(fromBedNo);
        record.setToWardId(dto.getToWardId());
        record.setToWardName(toWard.getWardName());
        record.setToBedNo(dto.getToBedNo());
        record.setOperatorId(dto.getOperatorId());
        record.setOperatorName(dto.getOperatorName());
        record.setTransferTime(LocalDateTime.now());
        record.setRemark(dto.getRemark());
        transferRecordMapper.insert(record);

        patient.setWardId(dto.getToWardId());
        patient.setBedNo(dto.getToBedNo());
        patientMapper.updateById(patient);

        LambdaQueryWrapper<WardPatientConfig> configWrapper = new LambdaQueryWrapper<>();
        configWrapper.eq(WardPatientConfig::getPatientId, patient.getId());
        WardPatientConfig config = configMapper.selectOne(configWrapper);
        if (config != null) {
            config.setWardId(dto.getToWardId());
            config.setWardName(toWard.getWardName());
            configMapper.updateById(config);
        }

        return transferRecordMapper.getTransferDetailById(record.getId());
    }

    @Override
    public PageResult<PatientTransferRecordVO> getTransferPage(Long current, Long size, Long patientId,
                                                                Long fromWardId, Long toWardId,
                                                                String patientName) {
        Page<PatientTransferRecordVO> page = new Page<>(current, size);
        IPage<PatientTransferRecordVO> result = transferRecordMapper.selectTransferPage(page, patientId,
                fromWardId, toWardId, patientName);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }

    @Override
    public PatientTransferRecordVO getTransferDetail(Long id) {
        return transferRecordMapper.getTransferDetailById(id);
    }
}
