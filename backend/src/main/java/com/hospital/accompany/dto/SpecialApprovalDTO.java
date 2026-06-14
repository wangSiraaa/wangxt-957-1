package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class SpecialApprovalDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @NotNull(message = "陪护人员ID不能为空")
    private Long personId;

    @NotBlank(message = "审批原因不能为空")
    private String approvalReason;

    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    private Long applyUserId;

    private String applyUserName;
}
