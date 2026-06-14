package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CertLeaveRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String leaveNo;

    private Long certId;

    private String certNo;

    private Long patientId;

    private String patientName;

    private Long wardId;

    private String wardName;

    private Long personId;

    private String personName;

    private Integer leaveType;

    private LocalDateTime leaveTime;

    private LocalDateTime expectedReturnTime;

    private LocalDateTime actualReturnTime;

    private Integer leaveStatus;

    private String invalidReason;

    private LocalDateTime invalidTime;

    private Long operatorId;

    private String operatorName;
}
