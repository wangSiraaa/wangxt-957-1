package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class CertTransferDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "原陪护证ID不能为空")
    private Long oldCertId;

    @NotBlank(message = "交接原因不能为空")
    private String handoverReason;

    @NotBlank(message = "新陪护人员姓名不能为空")
    private String newPersonName;

    @NotBlank(message = "新陪护人员身份证号不能为空")
    private String newIdCard;

    @NotNull(message = "性别不能为空")
    private Integer newGender;

    @NotBlank(message = "联系电话不能为空")
    private String newPhone;

    @NotBlank(message = "与患者关系不能为空")
    private String newRelation;

    @NotNull(message = "身份证有效期不能为空")
    private LocalDate newIdCardExpireDate;

    private String newAddress;

    private Long operatorId;

    private String operatorName;
}
