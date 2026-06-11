package com.hospital.accompany.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ApplySubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @NotBlank(message = "陪护人员姓名不能为空")
    private String personName;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @NotBlank(message = "与患者关系不能为空")
    private String relation;

    @NotNull(message = "身份证有效期不能为空")
    private LocalDate idCardExpireDate;

    private String address;

    private String applyReason;

    @NotNull(message = "预计开始日期不能为空")
    private LocalDate expectedStartDate;

    @NotNull(message = "预计结束日期不能为空")
    private LocalDate expectedEndDate;
}
