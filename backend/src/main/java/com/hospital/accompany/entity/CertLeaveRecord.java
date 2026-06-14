package com.hospital.accompany.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("cert_leave_record")
public class CertLeaveRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String leaveNo;

    private Long certId;

    private String certNo;

    private Long patientId;

    private Long wardId;

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

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
