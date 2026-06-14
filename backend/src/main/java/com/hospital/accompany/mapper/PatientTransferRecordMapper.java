package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.PatientTransferRecord;
import com.hospital.accompany.vo.PatientTransferRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PatientTransferRecordMapper extends BaseMapper<PatientTransferRecord> {

    IPage<PatientTransferRecordVO> selectTransferPage(Page<PatientTransferRecordVO> page,
                                                       @Param("patientId") Long patientId,
                                                       @Param("fromWardId") Long fromWardId,
                                                       @Param("toWardId") Long toWardId,
                                                       @Param("patientName") String patientName);

    PatientTransferRecordVO getTransferDetailById(@Param("id") Long id);
}
