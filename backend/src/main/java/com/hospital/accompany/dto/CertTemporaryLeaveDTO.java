package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CertTemporaryLeaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "陪护证ID不能为空")
    private Long certId;

    @NotNull(message = "离院类型不能为空")
    private Integer leaveType;

    private LocalDateTime expectedReturnTime;

    private Long operatorId;

    private String operatorName;
}
