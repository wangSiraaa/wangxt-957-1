package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.SpecialApprovalAuditDTO;
import com.hospital.accompany.dto.SpecialApprovalDTO;
import com.hospital.accompany.service.SpecialApprovalService;
import com.hospital.accompany.vo.SpecialApprovalVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "隔离病区特殊审批")
@RestController
@RequestMapping("/special-approval")
public class SpecialApprovalController {

    @Autowired
    private SpecialApprovalService specialApprovalService;

    @Operation(summary = "发起特殊审批")
    @PostMapping("/submit")
    public Result<SpecialApprovalVO> submitSpecialApproval(@Valid @RequestBody SpecialApprovalDTO dto) {
        SpecialApprovalVO vo = specialApprovalService.submitSpecialApproval(dto);
        return Result.success("特殊审批已提交", vo);
    }

    @Operation(summary = "审核特殊审批")
    @PostMapping("/audit")
    public Result<SpecialApprovalVO> auditSpecialApproval(@Valid @RequestBody SpecialApprovalAuditDTO dto) {
        SpecialApprovalVO vo = specialApprovalService.auditSpecialApproval(dto);
        return Result.success("审核完成", vo);
    }

    @Operation(summary = "特殊审批列表")
    @GetMapping("/page")
    public Result<PageResult<SpecialApprovalVO>> getApprovalPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "审批状态 0-待审核 1-审核通过 2-审核拒绝") @RequestParam(required = false) Integer approvalStatus,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String patientName,
            @Parameter(description = "陪护人员姓名") @RequestParam(required = false) String personName) {
        PageResult<SpecialApprovalVO> page = specialApprovalService.getApprovalPage(current, size,
                wardId, approvalStatus, patientName, personName);
        return Result.success(page);
    }

    @Operation(summary = "特殊审批详情")
    @GetMapping("/{id}")
    public Result<SpecialApprovalVO> getApprovalDetail(@PathVariable Long id) {
        SpecialApprovalVO vo = specialApprovalService.getApprovalDetail(id);
        return Result.success(vo);
    }
}
