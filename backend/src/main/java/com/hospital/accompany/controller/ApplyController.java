package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.ApplySubmitDTO;
import com.hospital.accompany.service.AccompanyApplyService;
import com.hospital.accompany.vo.ApplyVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "陪护申请管理")
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private AccompanyApplyService applyService;

    @Operation(summary = "提交陪护申请")
    @PostMapping("/submit")
    public Result<String> submitApply(@Valid @RequestBody ApplySubmitDTO dto) {
        String applyNo = applyService.submitApply(dto);
        return Result.success("申请提交成功", applyNo);
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public Result<ApplyVO> getApplyDetail(@Parameter(description = "申请ID") @PathVariable Long id) {
        ApplyVO apply = applyService.getApplyDetail(id);
        return Result.success(apply);
    }

    @Operation(summary = "分页查询申请列表")
    @GetMapping("/page")
    public Result<PageResult<ApplyVO>> getApplyPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "申请状态") @RequestParam(required = false) Integer applyStatus,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String patientName,
            @Parameter(description = "陪护人员姓名") @RequestParam(required = false) String personName) {
        PageResult<ApplyVO> page = applyService.getApplyPage(current, size, wardId, applyStatus,
                patientName, personName);
        return Result.success(page);
    }
}
