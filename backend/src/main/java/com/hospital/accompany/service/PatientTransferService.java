package com.hospital.accompany.service;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.PatientTransferWardDTO;
import com.hospital.accompany.vo.PatientTransferRecordVO;

public interface PatientTransferService {

    PatientTransferRecordVO transferWard(PatientTransferWardDTO dto);

    PageResult<PatientTransferRecordVO> getTransferPage(Long current, Long size, Long patientId,
                                                         Long fromWardId, Long toWardId,
                                                         String patientName);

    PatientTransferRecordVO getTransferDetail(Long id);
}
