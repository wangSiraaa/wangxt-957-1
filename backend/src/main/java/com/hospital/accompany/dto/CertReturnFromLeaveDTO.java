package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CertReturnFromLeaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "离院记录ID不能为空")
    private Long leaveRecordId;

    private Long operatorId;

    private String operatorName;
}
