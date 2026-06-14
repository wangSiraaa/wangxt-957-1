package com.hospital.accompany.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AuditDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "申请ID不能为空")
    private Long applyId;

    @NotNull(message = "审核结果不能为空")
    private Integer auditResult;

    private String auditRemark;

    private Long auditUserId;

    private String auditUserName;
}
