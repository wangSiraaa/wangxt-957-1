package com.hospital.accompany.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("cert_transfer_record")
public class CertTransferRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String transferNo;

    private Long patientId;

    private Long wardId;

    private Long oldCertId;

    private String oldCertNo;

    private Long oldPersonId;

    private String oldPersonName;

    private Long newCertId;

    private String newCertNo;

    private Long newPersonId;

    private String newPersonName;

    private String handoverReason;

    private Long operatorId;

    private String operatorName;

    private LocalDateTime transferTime;

    private Integer newApplyStatus;

    private Long newApplyId;

    private Long newAuditUserId;

    private String newAuditUserName;

    private LocalDateTime newAuditTime;

    private String newAuditRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
