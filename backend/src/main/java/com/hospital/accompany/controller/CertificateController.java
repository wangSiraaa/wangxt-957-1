package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.CertificateInvalidDTO;
import com.hospital.accompany.service.CertificateService;
import com.hospital.accompany.vo.CertificateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "陪护证管理")
@RestController
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @ApiOperation("获取陪护证详情")
    @GetMapping("/{id}")
    public Result<CertificateVO> getCertificateDetail(@ApiParam("陪护证ID") @PathVariable Long id) {
        CertificateVO cert = certificateService.getCertificateDetail(id);
        return Result.success(cert);
    }

    @ApiOperation("根据证件编号查询陪护证")
    @GetMapping("/no/{certNo}")
    public Result<CertificateVO> getCertificateByNo(@ApiParam("陪护证编号") @PathVariable String certNo) {
        CertificateVO cert = certificateService.getCertificateByNo(certNo);
        return Result.success(cert);
    }

    @ApiOperation("分页查询陪护证列表")
    @GetMapping("/page")
    public Result<PageResult<CertificateVO>> getCertificatePage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("病区ID") @RequestParam(required = false) Long wardId,
            @ApiParam("证件状态 0-已失效 1-有效") @RequestParam(required = false) Integer certStatus,
            @ApiParam("患者姓名") @RequestParam(required = false) String patientName,
            @ApiParam("陪护人员姓名") @RequestParam(required = false) String personName,
            @ApiParam("陪护证编号") @RequestParam(required = false) String certNo) {
        PageResult<CertificateVO> page = certificateService.getCertificatePage(current, size, wardId,
                certStatus, patientName, personName, certNo);
        return Result.success(page);
    }

    @ApiOperation("陪护证失效处理")
    @PostMapping("/invalid")
    public Result<CertificateVO> invalidCertificate(@Valid @RequestBody CertificateInvalidDTO dto) {
        CertificateVO cert = certificateService.invalidCertificate(dto);
        return Result.success("失效处理成功", cert);
    }

    @ApiOperation("获取病区有效陪护证数量")
    @GetMapping("/count/{wardId}")
    public Result<Integer> getValidCountByWard(@ApiParam("病区ID") @PathVariable Long wardId) {
        Integer count = certificateService.getValidCountByWard(wardId);
        return Result.success(count);
    }
}
