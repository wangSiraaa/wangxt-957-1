package com.hospital.accompany.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("accompany_apply")
public class AccompanyApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String applyNo;

    private Long patientId;

    private Long wardId;

    private Long personId;

    private String applyReason;

    private LocalDate expectedStartDate;

    private LocalDate expectedEndDate;

    private Integer applyStatus;

    private Long auditUserId;

    private String auditUserName;

    private LocalDateTime auditTime;

    private String auditRemark;

    private String invalidReason;

    private LocalDateTime invalidTime;

    private LocalDateTime applyTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
