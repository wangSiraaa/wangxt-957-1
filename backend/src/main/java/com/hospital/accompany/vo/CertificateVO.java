package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CertificateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String certNo;

    private Long applyId;

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

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer certStatus;

    private String issueUserName;

    private LocalDateTime issueTime;

    private String invalidReason;

    private LocalDateTime invalidTime;

    private String invalidOperatorName;

    private LocalDate idCardExpireDate;

    private Integer certType;

    private Long specialApprovalId;

    private Long currentLeaveId;

    private Integer todayInCount;

    private Integer todayOutCount;

    private Integer isIsolationWard;
}
