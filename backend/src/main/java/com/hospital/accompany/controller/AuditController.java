package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.AuditDTO;
import com.hospital.accompany.service.AuditService;
import com.hospital.accompany.vo.ApplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "护士站审核管理")
@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @ApiOperation("审核陪护申请")
    @PostMapping("/audit")
    public Result<ApplyVO> auditApply(@Valid @RequestBody AuditDTO dto) {
        ApplyVO apply = auditService.auditApply(dto);
        return Result.success("审核完成", apply);
    }

    @ApiOperation("获取待审核列表")
    @GetMapping("/pending")
    public Result<PageResult<ApplyVO>> getPendingList(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("病区ID") @RequestParam(required = false) Long wardId,
            @ApiParam("患者姓名") @RequestParam(required = false) String patientName,
            @ApiParam("陪护人员姓名") @RequestParam(required = false) String personName) {
        PageResult<ApplyVO> page = auditService.getPendingList(current, size, wardId, patientName, personName);
        return Result.success(page);
    }
}
