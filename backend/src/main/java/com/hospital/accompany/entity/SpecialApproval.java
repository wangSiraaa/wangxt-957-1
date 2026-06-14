package com.hospital.accompany.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("special_approval")
public class SpecialApproval implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String approvalNo;

    private Long patientId;

    private String patientName;

    private Long wardId;

    private String wardName;

    private Long personId;

    private String personName;

    private String idCard;

    private String approvalReason;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer approvalStatus;

    private Long applyUserId;

    private String applyUserName;

    private LocalDateTime applyTime;

    private Long auditUserId;

    private String auditUserName;

    private LocalDateTime auditTime;

    private String auditRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
