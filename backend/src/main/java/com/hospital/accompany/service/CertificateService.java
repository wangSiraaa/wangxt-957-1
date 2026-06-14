package com.hospital.accompany.service;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.CertReturnFromLeaveDTO;
import com.hospital.accompany.dto.CertTemporaryLeaveDTO;
import com.hospital.accompany.dto.CertTransferDTO;
import com.hospital.accompany.dto.CertificateInvalidDTO;
import com.hospital.accompany.vo.CertLeaveRecordVO;
import com.hospital.accompany.vo.CertTransferRecordVO;
import com.hospital.accompany.vo.CertificateVO;

public interface CertificateService {

    CertificateVO getCertificateDetail(Long id);

    CertificateVO getCertificateByNo(String certNo);

    PageResult<CertificateVO> getCertificatePage(Long current, Long size,
                                                  Long wardId, Integer certStatus,
                                                  String patientName, String personName,
                                                  String certNo);

    CertificateVO invalidCertificate(CertificateInvalidDTO dto);

    Integer getValidCountByWard(Long wardId);

    CertTransferRecordVO transferCompanion(CertTransferDTO dto);

    CertLeaveRecordVO temporaryLeave(CertTemporaryLeaveDTO dto);

    CertLeaveRecordVO returnFromLeave(CertReturnFromLeaveDTO dto);

    PageResult<CertTransferRecordVO> getTransferPage(Long current, Long size, Long patientId,
                                                      Long wardId, String oldPersonName, String newPersonName);

    CertTransferRecordVO getTransferDetail(Long id);

    PageResult<CertLeaveRecordVO> getLeavePage(Long current, Long size, Long certId,
                                                Long patientId, Long wardId,
                                                Integer leaveStatus, String personName);

    CertLeaveRecordVO getLeaveDetail(Long id);
}
