package com.hospital.accompany.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("accompany_certificate")
public class AccompanyCertificate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String certNo;

    private Long applyId;

    private Long patientId;

    private Long wardId;

    private Long personId;

    private Integer certType;

    private Long specialApprovalId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer certStatus;

    private Long currentLeaveId;

    private Long issueUserId;

    private String issueUserName;

    private LocalDateTime issueTime;

    private String invalidReason;

    private LocalDateTime invalidTime;

    private Long invalidOperatorId;

    private String invalidOperatorName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
