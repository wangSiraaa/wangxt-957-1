package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

    IPage<Patient> selectPatientPage(Page<Patient> page,
                                      @Param("wardId") Long wardId,
                                      @Param("patientName") String patientName,
                                      @Param("patientNo") String patientNo);
}
