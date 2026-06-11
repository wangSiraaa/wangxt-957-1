package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.ApplySubmitDTO;
import com.hospital.accompany.service.AccompanyApplyService;
import com.hospital.accompany.vo.ApplyVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "陪护申请管理")
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private AccompanyApplyService applyService;

    @ApiOperation("提交陪护申请")
    @PostMapping("/submit")
    public Result<String> submitApply(@Valid @RequestBody ApplySubmitDTO dto) {
        String applyNo = applyService.submitApply(dto);
        return Result.success("申请提交成功", applyNo);
    }

    @ApiOperation("获取申请详情")
    @GetMapping("/{id}")
    public Result<ApplyVO> getApplyDetail(@ApiParam("申请ID") @PathVariable Long id) {
        ApplyVO apply = applyService.getApplyDetail(id);
        return Result.success(apply);
    }

    @ApiOperation("分页查询申请列表")
    @GetMapping("/page")
    public Result<PageResult<ApplyVO>> getApplyPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("病区ID") @RequestParam(required = false) Long wardId,
            @ApiParam("申请状态") @RequestParam(required = false) Integer applyStatus,
            @ApiParam("患者姓名") @RequestParam(required = false) String patientName,
            @ApiParam("陪护人员姓名") @RequestParam(required = false) String personName) {
        PageResult<ApplyVO> page = applyService.getApplyPage(current, size, wardId, applyStatus,
                patientName, personName);
        return Result.success(page);
    }
}
