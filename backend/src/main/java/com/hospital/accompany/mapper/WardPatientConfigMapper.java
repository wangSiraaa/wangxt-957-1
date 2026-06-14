package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.WardPatientConfig;
import com.hospital.accompany.vo.WardPatientConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WardPatientConfigMapper extends BaseMapper<WardPatientConfig> {

    IPage<WardPatientConfigVO> selectConfigPage(Page<WardPatientConfigVO> page,
                                                 @Param("wardId") Long wardId,
                                                 @Param("patientName") String patientName);

    WardPatientConfigVO getConfigByPatientId(@Param("patientId") Long patientId);
}
