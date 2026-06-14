package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.AuditDTO;
import com.hospital.accompany.service.AuditService;
import com.hospital.accompany.vo.ApplyVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "护士站审核管理")
@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Operation(summary = "审核陪护申请")
    @PostMapping("/audit")
    public Result<ApplyVO> auditApply(@Valid @RequestBody AuditDTO dto) {
        ApplyVO apply = auditService.auditApply(dto);
        return Result.success("审核完成", apply);
    }

    @Operation(summary = "获取待审核列表")
    @GetMapping("/pending")
    public Result<PageResult<ApplyVO>> getPendingList(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String patientName,
            @Parameter(description = "陪护人员姓名") @RequestParam(required = false) String personName) {
        PageResult<ApplyVO> page = auditService.getPendingList(current, size, wardId, patientName, personName);
        return Result.success(page);
    }
}
