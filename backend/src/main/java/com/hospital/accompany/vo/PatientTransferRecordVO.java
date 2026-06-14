package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PatientTransferRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String transferNo;

    private Long patientId;

    private String patientName;

    private String patientNo;

    private Long fromWardId;

    private String fromWardName;

    private String fromBedNo;

    private Long toWardId;

    private String toWardName;

    private String toBedNo;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime transferTime;

    private String remark;
}
