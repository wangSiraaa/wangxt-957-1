package com.hospital.accompany.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.entity.SpecialApproval;
import com.hospital.accompany.vo.SpecialApprovalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SpecialApprovalMapper extends BaseMapper<SpecialApproval> {

    IPage<SpecialApprovalVO> selectApprovalPage(Page<SpecialApprovalVO> page,
                                                 @Param("wardId") Long wardId,
                                                 @Param("approvalStatus") Integer approvalStatus,
                                                 @Param("patientName") String patientName,
                                                 @Param("personName") String personName);

    SpecialApprovalVO getApprovalDetailById(@Param("id") Long id);
}
