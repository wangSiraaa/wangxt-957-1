package com.hospital.accompany.service;

import com.hospital.accompany.dto.GateCheckDTO;
import com.hospital.accompany.vo.CertificateVO;
import com.hospital.accompany.vo.GateRecordVO;

public interface GateService {

    CertificateVO checkCertificate(String certNoOrIdCard);

    GateRecordVO gateCheck(GateCheckDTO dto);

    com.hospital.accompany.common.PageResult<GateRecordVO> getRecordPage(Long current, Long size,
                                                                          Long wardId, Integer gateType,
                                                                          Integer checkResult, String personName,
                                                                          String startDate, String endDate);
}
