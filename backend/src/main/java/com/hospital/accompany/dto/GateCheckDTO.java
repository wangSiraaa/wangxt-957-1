package com.hospital.accompany.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class GateCheckDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "证件编号或身份证号不能为空")
    private String certNoOrIdCard;

    @NotNull(message = "闸机类型不能为空")
    private Integer gateType;

    private Long gateUserId;

    private String gateUserName;
}
