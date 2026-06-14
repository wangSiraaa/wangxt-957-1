package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class WardPatientConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long patientId;

    private String patientName;

    private String patientNo;

    private String bedNo;

    private Long wardId;

    private String wardName;

    private Integer maxAccompanyCount;

    private String adjustReason;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
