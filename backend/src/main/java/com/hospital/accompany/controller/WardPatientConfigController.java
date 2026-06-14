package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.AdjustAccompanyCountDTO;
import com.hospital.accompany.service.WardPatientConfigService;
import com.hospital.accompany.vo.WardPatientConfigVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "陪护人数配置")
@RestController
@RequestMapping("/ward-patient-config")
public class WardPatientConfigController {

    @Autowired
    private WardPatientConfigService wardPatientConfigService;

    @Operation(summary = "调整患者陪护人数上限")
    @PostMapping("/adjust")
    public Result<WardPatientConfigVO> adjustAccompanyCount(@Valid @RequestBody AdjustAccompanyCountDTO dto) {
        WardPatientConfigVO vo = wardPatientConfigService.adjustAccompanyCount(dto);
        return Result.success("陪护人数上限调整成功", vo);
    }

    @Operation(summary = "获取患者陪护配置")
    @GetMapping("/patient/{patientId}")
    public Result<WardPatientConfigVO> getConfigByPatientId(@PathVariable Long patientId) {
        WardPatientConfigVO vo = wardPatientConfigService.getConfigByPatientId(patientId);
        return Result.success(vo);
    }

    @Operation(summary = "陪护配置列表")
    @GetMapping("/page")
    public Result<PageResult<WardPatientConfigVO>> getConfigPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String patientName) {
        PageResult<WardPatientConfigVO> page = wardPatientConfigService.getConfigPage(current, size,
                wardId, patientName);
        return Result.success(page);
    }
}
