package com.hospital.accompany.service;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.SpecialApprovalAuditDTO;
import com.hospital.accompany.dto.SpecialApprovalDTO;
import com.hospital.accompany.vo.SpecialApprovalVO;

public interface SpecialApprovalService {

    SpecialApprovalVO submitSpecialApproval(SpecialApprovalDTO dto);

    SpecialApprovalVO auditSpecialApproval(SpecialApprovalAuditDTO dto);

    PageResult<SpecialApprovalVO> getApprovalPage(Long current, Long size, Long wardId,
                                                    Integer approvalStatus, String patientName,
                                                    String personName);

    SpecialApprovalVO getApprovalDetail(Long id);
}
