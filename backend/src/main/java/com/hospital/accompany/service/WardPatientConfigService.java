package com.hospital.accompany.service;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.dto.AdjustAccompanyCountDTO;
import com.hospital.accompany.vo.WardPatientConfigVO;

public interface WardPatientConfigService {

    WardPatientConfigVO adjustAccompanyCount(AdjustAccompanyCountDTO dto);

    WardPatientConfigVO getConfigByPatientId(Long patientId);

    PageResult<WardPatientConfigVO> getConfigPage(Long current, Long size, Long wardId, String patientName);
}
