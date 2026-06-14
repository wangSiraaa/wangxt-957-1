package com.hospital.accompany.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.SpecialApprovalAuditDTO;
import com.hospital.accompany.dto.SpecialApprovalDTO;
import com.hospital.accompany.entity.*;
import com.hospital.accompany.mapper.*;
import com.hospital.accompany.service.SpecialApprovalService;
import com.hospital.accompany.vo.SpecialApprovalVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SpecialApprovalServiceImpl implements SpecialApprovalService {

    @Autowired
    private SpecialApprovalMapper specialApprovalMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WardMapper wardMapper;

    @Autowired
    private AccompanyPersonMapper personMapper;

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Autowired
    private AccompanyApplyMapper applyMapper;

    @Autowired
    private WardPatientConfigMapper configMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpecialApprovalVO submitSpecialApproval(SpecialApprovalDTO dto) {
        Patient patient = patientMapper.selectById(dto.getPatientId());
        if (patient == null || patient.getStatus() != 1) {
            throw new BusinessException("患者不存在或已出院");
        }

        Ward ward = wardMapper.selectById(patient.getWardId());
        if (ward == null) {
            throw new BusinessException("病区不存在");
        }
        if (ward.getIsIsolation() != 1) {
            throw new BusinessException("特殊审批仅适用于隔离病区");
        }

        AccompanyPerson person = personMapper.selectById(dto.getPersonId());
        if (person == null) {
            throw new BusinessException("陪护人员不存在");
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        SpecialApproval approval = new SpecialApproval();
        String approvalNo = "TSSP" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
        approval.setApprovalNo(approvalNo);
        approval.setPatientId(patient.getId());
        approval.setPatientName(patient.getPatientName());
        approval.setWardId(ward.getId());
        approval.setWardName(ward.getWardName());
        approval.setPersonId(person.getId());
        approval.setPersonName(person.getPersonName());
        approval.setIdCard(person.getIdCard());
        approval.setApprovalReason(dto.getApprovalReason());
        approval.setStartDate(dto.getStartDate());
        approval.setEndDate(dto.getEndDate());
        approval.setApprovalStatus(0);
        approval.setApplyUserId(dto.getApplyUserId());
        approval.setApplyUserName(dto.getApplyUserName());
        approval.setApplyTime(LocalDateTime.now());
        specialApprovalMapper.insert(approval);

        return specialApprovalMapper.getApprovalDetailById(approval.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpecialApprovalVO auditSpecialApproval(SpecialApprovalAuditDTO dto) {
        SpecialApproval approval = specialApprovalMapper.selectById(dto.getApprovalId());
        if (approval == null) {
            throw new BusinessException("审批记录不存在");
        }
        if (approval.getApprovalStatus() != 0) {
            throw new BusinessException("该审批已审核，不能重复审核");
        }

        approval.setApprovalStatus(dto.getAuditResult() == 1 ? 1 : 2);
        approval.setAuditUserId(dto.getAuditUserId());
        approval.setAuditUserName(dto.getAuditUserName());
        approval.setAuditTime(LocalDateTime.now());
        approval.setAuditRemark(dto.getAuditRemark());
        specialApprovalMapper.updateById(approval);

        if (dto.getAuditResult() == 1) {
            Patient patient = patientMapper.selectById(approval.getPatientId());
            Ward ward = wardMapper.selectById(approval.getWardId());

            int maxCount = ward.getMaxAccompanyPerBed();
            WardPatientConfig config = configMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<WardPatientConfig>()
                            .eq(WardPatientConfig::getPatientId, approval.getPatientId()));
            if (config != null) {
                maxCount = config.getMaxAccompanyCount();
            }

            Integer validCount = certificateMapper.countValidByPatientId(approval.getPatientId());
            if (validCount >= maxCount) {
                throw new BusinessException("陪护人数已超过限制（上限" + maxCount + "人）");
            }

            AccompanyCertificate cert = new AccompanyCertificate();
            String certNo = "PHZ" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                    String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
            cert.setCertNo(certNo);
            cert.setPatientId(approval.getPatientId());
            cert.setWardId(approval.getWardId());
            cert.setPersonId(approval.getPersonId());
            cert.setCertType(2);
            cert.setSpecialApprovalId(approval.getId());
            cert.setStartDate(approval.getStartDate());
            cert.setEndDate(approval.getEndDate());
            cert.setCertStatus(1);
            cert.setIssueUserId(dto.getAuditUserId());
            cert.setIssueUserName(dto.getAuditUserName());
            cert.setIssueTime(LocalDateTime.now());
            certificateMapper.insert(cert);

            AccompanyApply apply = new AccompanyApply();
            String applyNo = "SQ" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                    String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
            apply.setApplyNo(applyNo);
            apply.setPatientId(approval.getPatientId());
            apply.setWardId(approval.getWardId());
            apply.setPersonId(approval.getPersonId());
            apply.setApplyType(3);
            apply.setSpecialApprovalId(approval.getId());
            apply.setApplyReason("隔离病区特殊审批");
            apply.setExpectedStartDate(approval.getStartDate());
            apply.setExpectedEndDate(approval.getEndDate());
            apply.setApplyStatus(1);
            apply.setAuditUserId(dto.getAuditUserId());
            apply.setAuditUserName(dto.getAuditUserName());
            apply.setAuditTime(LocalDateTime.now());
            apply.setApplyTime(approval.getApplyTime());
            applyMapper.insert(apply);

            cert.setApplyId(apply.getId());
            certificateMapper.updateById(cert);
        }

        return specialApprovalMapper.getApprovalDetailById(approval.getId());
    }

    @Override
    public PageResult<SpecialApprovalVO> getApprovalPage(Long current, Long size, Long wardId,
                                                           Integer approvalStatus, String patientName,
                                                           String personName) {
        Page<SpecialApprovalVO> page = new Page<>(current, size);
        IPage<SpecialApprovalVO> result = specialApprovalMapper.selectApprovalPage(page, wardId,
                approvalStatus, patientName, personName);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }

    @Override
    public SpecialApprovalVO getApprovalDetail(Long id) {
        return specialApprovalMapper.getApprovalDetailById(id);
    }
}
