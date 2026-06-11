package com.hospital.accompany.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.accompany.dto.ApplySubmitDTO;
import com.hospital.accompany.entity.AccompanyApply;
import com.hospital.accompany.vo.ApplyVO;

public interface AccompanyApplyService extends IService<AccompanyApply> {

    String submitApply(ApplySubmitDTO dto);

    ApplyVO getApplyDetail(Long id);

    com.hospital.accompany.common.PageResult<ApplyVO> getApplyPage(Long current, Long size, Long wardId,
                                                                   Integer applyStatus, String patientName,
                                                                   String personName);
}
