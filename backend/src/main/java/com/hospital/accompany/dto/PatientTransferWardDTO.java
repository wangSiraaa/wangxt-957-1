package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class PatientTransferWardDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @NotNull(message = "目标病区ID不能为空")
    private Long toWardId;

    private String toBedNo;

    private Long operatorId;

    private String operatorName;

    private String remark;
}
