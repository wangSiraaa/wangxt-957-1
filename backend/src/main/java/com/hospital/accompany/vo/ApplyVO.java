package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String applyNo;

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

    private String applyReason;

    private LocalDate expectedStartDate;

    private LocalDate expectedEndDate;

    private Integer applyStatus;

    private String auditUserName;

    private LocalDateTime auditTime;

    private String auditRemark;

    private String invalidReason;

    private LocalDateTime invalidTime;

    private LocalDateTime applyTime;
}
