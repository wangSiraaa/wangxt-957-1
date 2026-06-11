package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.GateRecord;
import com.hospital.accompany.vo.GateRecordVO;
import org.apache.ibatis.annotations.Param;

public interface GateRecordMapper extends BaseMapper<GateRecord> {

    IPage<GateRecordVO> selectRecordPage(Page<GateRecordVO> page,
                                          @Param("wardId") Long wardId,
                                          @Param("gateType") Integer gateType,
                                          @Param("checkResult") Integer checkResult,
                                          @Param("personName") String personName,
                                          @Param("startDate") String startDate,
                                          @Param("endDate") String endDate);
}
