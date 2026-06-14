package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.PatientTransferWardDTO;
import com.hospital.accompany.service.PatientTransferService;
import com.hospital.accompany.vo.PatientTransferRecordVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "患者转病区管理")
@RestController
@RequestMapping("/patient/transfer")
public class PatientTransferController {

    @Autowired
    private PatientTransferService patientTransferService;

    @Operation(summary = "患者转病区")
    @PostMapping
    public Result<PatientTransferRecordVO> transferWard(@Valid @RequestBody PatientTransferWardDTO dto) {
        PatientTransferRecordVO vo = patientTransferService.transferWard(dto);
        return Result.success("转病区成功，原陪护证已自动失效", vo);
    }

    @Operation(summary = "转病区记录列表")
    @GetMapping("/page")
    public Result<PageResult<PatientTransferRecordVO>> getTransferPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
            @Parameter(description = "原病区ID") @RequestParam(required = false) Long fromWardId,
            @Parameter(description = "目标病区ID") @RequestParam(required = false) Long toWardId,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String patientName) {
        PageResult<PatientTransferRecordVO> page = patientTransferService.getTransferPage(current, size,
                patientId, fromWardId, toWardId, patientName);
        return Result.success(page);
    }

    @Operation(summary = "转病区记录详情")
    @GetMapping("/{id}")
    public Result<PatientTransferRecordVO> getTransferDetail(@PathVariable Long id) {
        PatientTransferRecordVO vo = patientTransferService.getTransferDetail(id);
        return Result.success(vo);
    }
}
