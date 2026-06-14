package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CertificateInvalidDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "陪护证ID不能为空")
    private Long certId;

    @NotBlank(message = "失效原因不能为空")
    private String invalidReason;

    private Long operatorId;

    private String operatorName;
}
