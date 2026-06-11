package com.hospital.accompany.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GateRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String recordNo;

    private Long certId;

    private String certNo;

    private Long personId;

    private String personName;

    private String idCard;

    private Long wardId;

    private String wardName;

    private Integer gateType;

    private Integer checkResult;

    private String refuseReason;

    private String gateUserName;

    private LocalDateTime checkTime;
}
