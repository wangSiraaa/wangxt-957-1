package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.accompany.entity.AccompanyApply;
import com.hospital.accompany.vo.ApplyVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AccompanyApplyMapper extends BaseMapper<AccompanyApply> {

    @Select("SELECT COUNT(*) FROM accompany_apply " +
            "WHERE patient_id = #{patientId} AND apply_status = 1 AND deleted = 0")
    Integer countValidByPatientId(@Param("patientId") Long patientId);

    ApplyVO getApplyDetailById(@Param("id") Long id);

    com.baomidou.mybatisplus.core.metadata.IPage<ApplyVO> selectApplyPage(
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<ApplyVO> page,
            @Param("wardId") Long wardId,
            @Param("applyStatus") Integer applyStatus,
            @Param("patientName") String patientName,
            @Param("personName") String personName);
}
