package com.hospital.accompany.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.AdjustAccompanyCountDTO;
import com.hospital.accompany.entity.Patient;
import com.hospital.accompany.entity.Ward;
import com.hospital.accompany.entity.WardPatientConfig;
import com.hospital.accompany.mapper.PatientMapper;
import com.hospital.accompany.mapper.WardMapper;
import com.hospital.accompany.mapper.WardPatientConfigMapper;
import com.hospital.accompany.service.WardPatientConfigService;
import com.hospital.accompany.vo.WardPatientConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WardPatientConfigServiceImpl implements WardPatientConfigService {

    @Autowired
    private WardPatientConfigMapper configMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WardMapper wardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WardPatientConfigVO adjustAccompanyCount(AdjustAccompanyCountDTO dto) {
        Patient patient = patientMapper.selectById(dto.getPatientId());
        if (patient == null || patient.getStatus() != 1) {
            throw new BusinessException("患者不存在或已出院");
        }

        Ward ward = wardMapper.selectById(patient.getWardId());
        if (ward == null) {
            throw new BusinessException("病区不存在");
        }

        if (dto.getMaxAccompanyCount() < 0) {
            throw new BusinessException("陪护人数上限不能为负数");
        }

        LambdaQueryWrapper<WardPatientConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WardPatientConfig::getPatientId, dto.getPatientId());
        WardPatientConfig config = configMapper.selectOne(wrapper);

        if (config == null) {
            config = new WardPatientConfig();
            config.setPatientId(patient.getId());
            config.setPatientName(patient.getPatientName());
            config.setWardId(patient.getWardId());
            config.setWardName(ward.getWardName());
            config.setMaxAccompanyCount(dto.getMaxAccompanyCount());
            config.setAdjustReason(dto.getAdjustReason());
            config.setOperatorId(dto.getOperatorId());
            config.setOperatorName(dto.getOperatorName());
            configMapper.insert(config);
        } else {
            config.setMaxAccompanyCount(dto.getMaxAccompanyCount());
            config.setAdjustReason(dto.getAdjustReason());
            config.setOperatorId(dto.getOperatorId());
            config.setOperatorName(dto.getOperatorName());
            config.setWardId(patient.getWardId());
            config.setWardName(ward.getWardName());
            configMapper.updateById(config);
        }

        return configMapper.getConfigByPatientId(dto.getPatientId());
    }

    @Override
    public WardPatientConfigVO getConfigByPatientId(Long patientId) {
        return configMapper.getConfigByPatientId(patientId);
    }

    @Override
    public PageResult<WardPatientConfigVO> getConfigPage(Long current, Long size, Long wardId,
                                                          String patientName) {
        Page<WardPatientConfigVO> page = new Page<>(current, size);
        IPage<WardPatientConfigVO> result = configMapper.selectConfigPage(page, wardId, patientName);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }
}
