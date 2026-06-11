package com.hospital.accompany.service;

import com.hospital.accompany.dto.AuditDTO;
import com.hospital.accompany.vo.ApplyVO;

public interface AuditService {

    ApplyVO auditApply(AuditDTO dto);

    com.hospital.accompany.common.PageResult<ApplyVO> getPendingList(Long current, Long size, Long wardId,
                                                                     String patientName, String personName);
}
