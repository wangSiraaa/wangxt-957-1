package com.hospital.accompany.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.CertificateInvalidDTO;
import com.hospital.accompany.entity.AccompanyApply;
import com.hospital.accompany.entity.AccompanyCertificate;
import com.hospital.accompany.mapper.AccompanyApplyMapper;
import com.hospital.accompany.mapper.AccompanyCertificateMapper;
import com.hospital.accompany.service.CertificateService;
import com.hospital.accompany.vo.CertificateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Autowired
    private AccompanyApplyMapper applyMapper;

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
}
