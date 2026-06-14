package com.hospital.accompany.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("patient_transfer_record")
public class PatientTransferRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String transferNo;

    private Long patientId;

    private String patientName;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
