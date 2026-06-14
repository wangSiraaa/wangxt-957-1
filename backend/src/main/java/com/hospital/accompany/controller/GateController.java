package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.GateCheckDTO;
import com.hospital.accompany.service.GateService;
import com.hospital.accompany.vo.CertificateVO;
import com.hospital.accompany.vo.GateRecordVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "门禁查验管理")
@RestController
@RequestMapping("/gate")
public class GateController {

    @Autowired
    private GateService gateService;

    @Operation(summary = "查询陪护证信息")
    @GetMapping("/check/{certNoOrIdCard}")
    public Result<CertificateVO> checkCertificate(
            @Parameter(description = "陪护证号或身份证号") @PathVariable String certNoOrIdCard) {
        CertificateVO cert = gateService.checkCertificate(certNoOrIdCard);
        return Result.success(cert);
    }

    @Operation(summary = "门禁查验（入院/出院）")
    @PostMapping("/check")
    public Result<GateRecordVO> gateCheck(@Valid @RequestBody GateCheckDTO dto) {
        GateRecordVO record = gateService.gateCheck(dto);
        String msg = record.getCheckResult() == 1 ? "查验通过" : "查验拒绝：" + record.getRefuseReason();
        return Result.success(msg, record);
    }

    @Operation(summary = "门禁记录列表")
    @GetMapping("/records")
    public Result<PageResult<GateRecordVO>> getRecordPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "闸机类型 1-入院 2-出院") @RequestParam(required = false) Integer gateType,
            @Parameter(description = "查验结果 1-通过 2-拒绝") @RequestParam(required = false) Integer checkResult,
            @Parameter(description = "陪护人员姓名") @RequestParam(required = false) String personName,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        PageResult<GateRecordVO> page = gateService.getRecordPage(current, size, wardId, gateType,
                checkResult, personName, startDate, endDate);
        return Result.success(page);
    }
}
