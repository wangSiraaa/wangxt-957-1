package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.GateCheckDTO;
import com.hospital.accompany.service.GateService;
import com.hospital.accompany.vo.CertificateVO;
import com.hospital.accompany.vo.GateRecordVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "门禁查验管理")
@RestController
@RequestMapping("/gate")
public class GateController {

    @Autowired
    private GateService gateService;

    @ApiOperation("查询陪护证信息")
    @GetMapping("/check/{certNoOrIdCard}")
    public Result<CertificateVO> checkCertificate(
            @ApiParam("陪护证号或身份证号") @PathVariable String certNoOrIdCard) {
        CertificateVO cert = gateService.checkCertificate(certNoOrIdCard);
        return Result.success(cert);
    }

    @ApiOperation("门禁查验（入院/出院）")
    @PostMapping("/check")
    public Result<GateRecordVO> gateCheck(@Valid @RequestBody GateCheckDTO dto) {
        GateRecordVO record = gateService.gateCheck(dto);
        String msg = record.getCheckResult() == 1 ? "查验通过" : "查验拒绝：" + record.getRefuseReason();
        return Result.success(msg, record);
    }

    @ApiOperation("门禁记录列表")
    @GetMapping("/records")
    public Result<PageResult<GateRecordVO>> getRecordPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("病区ID") @RequestParam(required = false) Long wardId,
            @ApiParam("闸机类型 1-入院 2-出院") @RequestParam(required = false) Integer gateType,
            @ApiParam("查验结果 1-通过 2-拒绝") @RequestParam(required = false) Integer checkResult,
            @ApiParam("陪护人员姓名") @RequestParam(required = false) String personName,
            @ApiParam("开始日期") @RequestParam(required = false) String startDate,
            @ApiParam("结束日期") @RequestParam(required = false) String endDate) {
        PageResult<GateRecordVO> page = gateService.getRecordPage(current, size, wardId, gateType,
                checkResult, personName, startDate, endDate);
        return Result.success(page);
    }
}
