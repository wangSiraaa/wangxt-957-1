package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.CertLeaveRecord;
import com.hospital.accompany.vo.CertLeaveRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CertLeaveRecordMapper extends BaseMapper<CertLeaveRecord> {

    IPage<CertLeaveRecordVO> selectLeavePage(Page<CertLeaveRecordVO> page,
                                              @Param("certId") Long certId,
                                              @Param("patientId") Long patientId,
                                              @Param("wardId") Long wardId,
                                              @Param("leaveStatus") Integer leaveStatus,
                                              @Param("personName") String personName);

    CertLeaveRecordVO getLeaveDetailById(@Param("id") Long id);

    List<CertLeaveRecord> selectOverdueLeaveRecords(@Param("now") LocalDateTime now);
}
