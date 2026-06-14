package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AdjustAccompanyCountDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @NotNull(message = "陪护人数上限不能为空")
    private Integer maxAccompanyCount;

    private String adjustReason;

    private Long operatorId;

    private String operatorName;
}
