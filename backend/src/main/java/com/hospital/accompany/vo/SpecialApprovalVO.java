package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SpecialApprovalVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String approvalNo;

    private Long patientId;

    private String patientName;

    private String patientNo;

    private String bedNo;

    private Long wardId;

    private String wardName;

    private Long personId;

    private String personName;

    private String idCard;

    private String phone;

    private String relation;

    private String approvalReason;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer approvalStatus;

    private Long applyUserId;

    private String applyUserName;

    private LocalDateTime applyTime;

    private Long auditUserId;

    private String auditUserName;

    private LocalDateTime auditTime;

    private String auditRemark;
}
