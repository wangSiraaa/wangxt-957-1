package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CertTransferRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String transferNo;

    private Long patientId;

    private String patientName;

    private Long wardId;

    private String wardName;

    private Long oldCertId;

    private String oldCertNo;

    private Long oldPersonId;

    private String oldPersonName;

    private Long newCertId;

    private String newCertNo;

    private Long newPersonId;

    private String newPersonName;

    private String handoverReason;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime transferTime;

    private Integer newApplyStatus;

    private String newAuditUserName;

    private LocalDateTime newAuditTime;

    private String newAuditRemark;
}
