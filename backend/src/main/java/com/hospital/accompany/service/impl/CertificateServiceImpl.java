package com.hospital.accompany.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.CertReturnFromLeaveDTO;
import com.hospital.accompany.dto.CertTemporaryLeaveDTO;
import com.hospital.accompany.dto.CertTransferDTO;
import com.hospital.accompany.dto.CertificateInvalidDTO;
import com.hospital.accompany.entity.*;
import com.hospital.accompany.mapper.*;
import com.hospital.accompany.service.CertificateService;
import com.hospital.accompany.vo.CertLeaveRecordVO;
import com.hospital.accompany.vo.CertTransferRecordVO;
import com.hospital.accompany.vo.CertificateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Autowired
    private AccompanyApplyMapper applyMapper;

    @Autowired
    private CertTransferRecordMapper transferRecordMapper;

    @Autowired
    private CertLeaveRecordMapper leaveRecordMapper;

    @Autowired
    private AccompanyPersonMapper personMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WardMapper wardMapper;

    @Override
    public CertificateVO getCertificateDetail(Long id) {
        return certificateMapper.getCertificateDetailById(id);
    }

    @Override
    public CertificateVO getCertificateByNo(String certNo) {
        return certificateMapper.getCertificateByNo(certNo);
    }

    @Override
    public PageResult<CertificateVO> getCertificatePage(Long current, Long size, Long wardId,
                                                         Integer certStatus, String patientName,
                                                         String personName, String certNo) {
        Page<CertificateVO> page = new Page<>(current, size);
        IPage<CertificateVO> result = certificateMapper.selectCertificatePage(page, wardId, certStatus,
                patientName, personName, certNo);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CertificateVO invalidCertificate(CertificateInvalidDTO dto) {
        AccompanyCertificate cert = certificateMapper.selectById(dto.getCertId());
        if (cert == null) {
            throw new BusinessException("陪护证不存在");
        }
        if (cert.getCertStatus() == 0) {
            throw new BusinessException("陪护证已失效，不能重复失效");
        }

        cert.setCertStatus(0);
        cert.setInvalidReason(dto.getInvalidReason());
        cert.setInvalidTime(LocalDateTime.now());
        cert.setInvalidOperatorId(dto.getOperatorId());
        cert.setInvalidOperatorName(dto.getOperatorName());
        certificateMapper.updateById(cert);

        if (cert.getApplyId() != null) {
            AccompanyApply apply = applyMapper.selectById(cert.getApplyId());
            if (apply != null) {
                apply.setApplyStatus(3);
                apply.setInvalidReason(dto.getInvalidReason());
                apply.setInvalidTime(LocalDateTime.now());
                applyMapper.updateById(apply);
            }
        }

        return getCertificateDetail(cert.getId());
    }

    @Override
    public Integer getValidCountByWard(Long wardId) {
        LambdaQueryWrapper<AccompanyCertificate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccompanyCertificate::getWardId, wardId)
                .eq(AccompanyCertificate::getCertStatus, 1);
        return certificateMapper.selectCount(wrapper).intValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CertTransferRecordVO transferCompanion(CertTransferDTO dto) {
        AccompanyCertificate oldCert = certificateMapper.selectById(dto.getOldCertId());
        if (oldCert == null) {
            throw new BusinessException("原陪护证不存在");
        }
        if (oldCert.getCertStatus() != 1) {
            throw new BusinessException("原陪护证不是有效状态，不能换陪护");
        }

        Patient patient = patientMapper.selectById(oldCert.getPatientId());
        if (patient == null) {
            throw new BusinessException("患者不存在");
        }

        AccompanyPerson oldPerson = personMapper.selectById(oldCert.getPersonId());

        AccompanyPerson newPerson;
        LambdaQueryWrapper<AccompanyPerson> personWrapper = new LambdaQueryWrapper<>();
        personWrapper.eq(AccompanyPerson::getIdCard, dto.getNewIdCard());
        newPerson = personMapper.selectOne(personWrapper);
        if (newPerson == null) {
            newPerson = new AccompanyPerson();
            newPerson.setPersonName(dto.getNewPersonName());
            newPerson.setIdCard(dto.getNewIdCard());
            newPerson.setGender(dto.getNewGender());
            newPerson.setPhone(dto.getNewPhone());
            newPerson.setRelation(dto.getNewRelation());
            newPerson.setIdCardExpireDate(dto.getNewIdCardExpireDate());
            newPerson.setAddress(dto.getNewAddress());
            personMapper.insert(newPerson);
        } else {
            newPerson.setPersonName(dto.getNewPersonName());
            newPerson.setGender(dto.getNewGender());
            newPerson.setPhone(dto.getNewPhone());
            newPerson.setRelation(dto.getNewRelation());
            newPerson.setIdCardExpireDate(dto.getNewIdCardExpireDate());
            newPerson.setAddress(dto.getNewAddress());
            personMapper.updateById(newPerson);
        }

        AccompanyApply apply = new AccompanyApply();
        String applyNo = "SQ" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
        apply.setApplyNo(applyNo);
        apply.setPatientId(oldCert.getPatientId());
        apply.setWardId(oldCert.getWardId());
        apply.setPersonId(newPerson.getId());
        apply.setApplyType(2);
        apply.setSourceCertId(oldCert.getId());
        apply.setApplyReason("换陪护：" + dto.getHandoverReason());
        apply.setExpectedStartDate(oldCert.getStartDate());
        apply.setExpectedEndDate(oldCert.getEndDate());
        apply.setApplyStatus(0);
        apply.setApplyTime(LocalDateTime.now());
        applyMapper.insert(apply);

        CertTransferRecord record = new CertTransferRecord();
        String transferNo = "HPH" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
        record.setTransferNo(transferNo);
        record.setPatientId(oldCert.getPatientId());
        record.setWardId(oldCert.getWardId());
        record.setOldCertId(oldCert.getId());
        record.setOldCertNo(oldCert.getCertNo());
        record.setOldPersonId(oldCert.getPersonId());
        record.setOldPersonName(oldPerson != null ? oldPerson.getPersonName() : "");
        record.setNewApplyId(apply.getId());
        record.setNewPersonId(newPerson.getId());
        record.setNewPersonName(newPerson.getPersonName());
        record.setHandoverReason(dto.getHandoverReason());
        record.setOperatorId(dto.getOperatorId());
        record.setOperatorName(dto.getOperatorName());
        record.setTransferTime(LocalDateTime.now());
        record.setNewApplyStatus(0);
        transferRecordMapper.insert(record);

        apply.setTransferRecordId(record.getId());
        applyMapper.updateById(apply);

        return transferRecordMapper.getTransferDetailById(record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CertLeaveRecordVO temporaryLeave(CertTemporaryLeaveDTO dto) {
        AccompanyCertificate cert = certificateMapper.selectById(dto.getCertId());
        if (cert == null) {
            throw new BusinessException("陪护证不存在");
        }
        if (cert.getCertStatus() != 1) {
            throw new BusinessException("陪护证不是有效状态，不能办理临时离院");
        }

        Patient patient = patientMapper.selectById(cert.getPatientId());
        AccompanyPerson person = personMapper.selectById(cert.getPersonId());

        cert.setCertStatus(2);
        certificateMapper.updateById(cert);

        CertLeaveRecord record = new CertLeaveRecord();
        String leaveNo = "LY" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
        record.setLeaveNo(leaveNo);
        record.setCertId(cert.getId());
        record.setCertNo(cert.getCertNo());
        record.setPatientId(cert.getPatientId());
        record.setWardId(cert.getWardId());
        record.setPersonId(cert.getPersonId());
        record.setPersonName(person != null ? person.getPersonName() : "");
        record.setLeaveType(dto.getLeaveType());
        record.setLeaveTime(LocalDateTime.now());
        record.setExpectedReturnTime(dto.getExpectedReturnTime());
        record.setLeaveStatus(1);
        record.setOperatorId(dto.getOperatorId());
        record.setOperatorName(dto.getOperatorName());
        leaveRecordMapper.insert(record);

        cert.setCurrentLeaveId(record.getId());
        certificateMapper.updateById(cert);

        return leaveRecordMapper.getLeaveDetailById(record.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CertLeaveRecordVO returnFromLeave(CertReturnFromLeaveDTO dto) {
        CertLeaveRecord record = leaveRecordMapper.selectById(dto.getLeaveRecordId());
        if (record == null) {
            throw new BusinessException("离院记录不存在");
        }
        if (record.getLeaveStatus() != 1) {
            throw new BusinessException("该离院记录不是离院中状态");
        }

        AccompanyCertificate cert = certificateMapper.selectById(record.getCertId());
        if (cert == null) {
            throw new BusinessException("陪护证不存在");
        }

        record.setLeaveStatus(2);
        record.setActualReturnTime(LocalDateTime.now());
        leaveRecordMapper.updateById(record);

        cert.setCertStatus(1);
        cert.setCurrentLeaveId(null);
        certificateMapper.updateById(cert);

        return leaveRecordMapper.getLeaveDetailById(record.getId());
    }

    @Override
    public PageResult<CertTransferRecordVO> getTransferPage(Long current, Long size, Long patientId,
                                                             Long wardId, String oldPersonName,
                                                             String newPersonName) {
        Page<CertTransferRecordVO> page = new Page<>(current, size);
        IPage<CertTransferRecordVO> result = transferRecordMapper.selectTransferPage(page, patientId,
                wardId, oldPersonName, newPersonName);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }

    @Override
    public CertTransferRecordVO getTransferDetail(Long id) {
        return transferRecordMapper.getTransferDetailById(id);
    }

    @Override
    public PageResult<CertLeaveRecordVO> getLeavePage(Long current, Long size, Long certId,
                                                       Long patientId, Long wardId,
                                                       Integer leaveStatus, String personName) {
        Page<CertLeaveRecordVO> page = new Page<>(current, size);
        IPage<CertLeaveRecordVO> result = leaveRecordMapper.selectLeavePage(page, certId,
                patientId, wardId, leaveStatus, personName);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }

    @Override
    public CertLeaveRecordVO getLeaveDetail(Long id) {
        return leaveRecordMapper.getLeaveDetailById(id);
    }
}
