package com.hospital.accompany.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.accompany.common.BusinessException;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.ApplySubmitDTO;
import com.hospital.accompany.entity.*;
import com.hospital.accompany.mapper.*;
import com.hospital.accompany.service.AccompanyApplyService;
import com.hospital.accompany.vo.ApplyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AccompanyApplyServiceImpl extends ServiceImpl<AccompanyApplyMapper, AccompanyApply>
        implements AccompanyApplyService {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private WardMapper wardMapper;

    @Autowired
    private AccompanyPersonMapper personMapper;

    @Autowired
    private AccompanyCertificateMapper certificateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitApply(ApplySubmitDTO dto) {
        Patient patient = patientMapper.selectById(dto.getPatientId());
        if (patient == null || patient.getStatus() != 1) {
            throw new BusinessException("患者不存在或已出院");
        }

        Ward ward = wardMapper.selectById(patient.getWardId());
        if (ward == null || ward.getStatus() != 1) {
            throw new BusinessException("病区不存在或已停用");
        }

        if (ward.getIsIsolation() == 1) {
            throw new BusinessException("隔离病区不能新增陪护");
        }

        if (dto.getExpectedEndDate().isBefore(dto.getExpectedStartDate())) {
            throw new BusinessException("结束日期不能早于开始日期");
        }

        if (dto.getIdCardExpireDate().isBefore(LocalDate.now())) {
            throw new BusinessException("身份证已过期，不能申请陪护");
        }

        Integer validCount = certificateMapper.countValidByPatientId(dto.getPatientId());
        if (validCount >= ward.getMaxAccompanyPerBed()) {
            throw new BusinessException("陪护人数已超过床位限制，不能新增陪护");
        }

        LambdaQueryWrapper<AccompanyPerson> personWrapper = new LambdaQueryWrapper<>();
        personWrapper.eq(AccompanyPerson::getIdCard, dto.getIdCard());
        AccompanyPerson person = personMapper.selectOne(personWrapper);
        if (person == null) {
            person = new AccompanyPerson();
            person.setPersonName(dto.getPersonName());
            person.setIdCard(dto.getIdCard());
            person.setGender(dto.getGender());
            person.setPhone(dto.getPhone());
            person.setRelation(dto.getRelation());
            person.setIdCardExpireDate(dto.getIdCardExpireDate());
            person.setAddress(dto.getAddress());
            personMapper.insert(person);
        } else {
            person.setPersonName(dto.getPersonName());
            person.setGender(dto.getGender());
            person.setPhone(dto.getPhone());
            person.setRelation(dto.getRelation());
            person.setIdCardExpireDate(dto.getIdCardExpireDate());
            person.setAddress(dto.getAddress());
            personMapper.updateById(person);
        }

        AccompanyApply apply = new AccompanyApply();
        String applyNo = "SQ" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                String.format("%06d", IdUtil.getSnowflakeNextId() % 1000000);
        apply.setApplyNo(applyNo);
        apply.setPatientId(dto.getPatientId());
        apply.setWardId(patient.getWardId());
        apply.setPersonId(person.getId());
        apply.setApplyReason(dto.getApplyReason());
        apply.setExpectedStartDate(dto.getExpectedStartDate());
        apply.setExpectedEndDate(dto.getExpectedEndDate());
        apply.setApplyStatus(0);
        apply.setApplyTime(LocalDateTime.now());
        baseMapper.insert(apply);

        return applyNo;
    }

    @Override
    public ApplyVO getApplyDetail(Long id) {
        return baseMapper.getApplyDetailById(id);
    }

    @Override
    public PageResult<ApplyVO> getApplyPage(Long current, Long size, Long wardId,
                                             Integer applyStatus, String patientName,
                                             String personName) {
        Page<ApplyVO> page = new Page<>(current, size);
        Page<ApplyVO> result = baseMapper.selectApplyPage(page, wardId, applyStatus, patientName, personName);
        return new PageResult<>(result.getTotal(), result.getRecords(), result.getCurrent(), result.getSize());
    }
}
