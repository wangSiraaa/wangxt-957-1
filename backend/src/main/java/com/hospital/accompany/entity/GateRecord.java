package com.hospital.accompany.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("gate_record")
public class GateRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
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

    private Long gateUserId;

    private String gateUserName;

    private LocalDateTime checkTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
