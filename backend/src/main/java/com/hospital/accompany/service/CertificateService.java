package com.hospital.accompany.service;

import com.hospital.accompany.dto.CertificateInvalidDTO;
import com.hospital.accompany.vo.CertificateVO;

public interface CertificateService {

    CertificateVO getCertificateDetail(Long id);

    CertificateVO getCertificateByNo(String certNo);

    com.hospital.accompany.common.PageResult<CertificateVO> getCertificatePage(Long current, Long size,
                                                                                Long wardId, Integer certStatus,
                                                                                String patientName, String personName,
                                                                                String certNo);

    CertificateVO invalidCertificate(CertificateInvalidDTO dto);

    Integer getValidCountByWard(Long wardId);
}
