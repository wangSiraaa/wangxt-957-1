package com.hospital.accompany.controller;

import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.dto.CertReturnFromLeaveDTO;
import com.hospital.accompany.dto.CertTemporaryLeaveDTO;
import com.hospital.accompany.dto.CertTransferDTO;
import com.hospital.accompany.dto.CertificateInvalidDTO;
import com.hospital.accompany.service.CertificateService;
import com.hospital.accompany.vo.CertLeaveRecordVO;
import com.hospital.accompany.vo.CertTransferRecordVO;
import com.hospital.accompany.vo.CertificateVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "陪护证管理")
@RestController
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Operation(summary = "获取陪护证详情")
    @GetMapping("/{id}")
    public Result<CertificateVO> getCertificateDetail(@Parameter(description = "陪护证ID") @PathVariable Long id) {
        CertificateVO cert = certificateService.getCertificateDetail(id);
        return Result.success(cert);
    }

    @Operation(summary = "根据证件编号查询陪护证")
    @GetMapping("/no/{certNo}")
    public Result<CertificateVO> getCertificateByNo(@Parameter(description = "陪护证编号") @PathVariable String certNo) {
        CertificateVO cert = certificateService.getCertificateByNo(certNo);
        return Result.success(cert);
    }

    @Operation(summary = "分页查询陪护证列表")
    @GetMapping("/page")
    public Result<PageResult<CertificateVO>> getCertificatePage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "证件状态 0-已失效 1-有效 2-临时离院") @RequestParam(required = false) Integer certStatus,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String patientName,
            @Parameter(description = "陪护人员姓名") @RequestParam(required = false) String personName,
            @Parameter(description = "陪护证编号") @RequestParam(required = false) String certNo) {
        PageResult<CertificateVO> page = certificateService.getCertificatePage(current, size, wardId,
                certStatus, patientName, personName, certNo);
        return Result.success(page);
    }

    @Operation(summary = "陪护证失效处理")
    @PostMapping("/invalid")
    public Result<CertificateVO> invalidCertificate(@Valid @RequestBody CertificateInvalidDTO dto) {
        CertificateVO cert = certificateService.invalidCertificate(dto);
        return Result.success("失效处理成功", cert);
    }

    @Operation(summary = "获取病区有效陪护证数量")
    @GetMapping("/count/{wardId}")
    public Result<Integer> getValidCountByWard(@Parameter(description = "病区ID") @PathVariable Long wardId) {
        Integer count = certificateService.getValidCountByWard(wardId);
        return Result.success(count);
    }

    @Operation(summary = "换陪护")
    @PostMapping("/transfer")
    public Result<CertTransferRecordVO> transferCompanion(@Valid @RequestBody CertTransferDTO dto) {
        CertTransferRecordVO vo = certificateService.transferCompanion(dto);
        return Result.success("换陪护申请已提交，等待审核", vo);
    }

    @Operation(summary = "临时离院")
    @PostMapping("/leave")
    public Result<CertLeaveRecordVO> temporaryLeave(@Valid @RequestBody CertTemporaryLeaveDTO dto) {
        CertLeaveRecordVO vo = certificateService.temporaryLeave(dto);
        return Result.success("临时离院办理成功", vo);
    }

    @Operation(summary = "离院返回")
    @PostMapping("/return")
    public Result<CertLeaveRecordVO> returnFromLeave(@Valid @RequestBody CertReturnFromLeaveDTO dto) {
        CertLeaveRecordVO vo = certificateService.returnFromLeave(dto);
        return Result.success("返回登记成功", vo);
    }

    @Operation(summary = "换陪护记录列表")
    @GetMapping("/transfer/page")
    public Result<PageResult<CertTransferRecordVO>> getTransferPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "原陪护人姓名") @RequestParam(required = false) String oldPersonName,
            @Parameter(description = "新陪护人姓名") @RequestParam(required = false) String newPersonName) {
        PageResult<CertTransferRecordVO> page = certificateService.getTransferPage(current, size,
                patientId, wardId, oldPersonName, newPersonName);
        return Result.success(page);
    }

    @Operation(summary = "换陪护记录详情")
    @GetMapping("/transfer/{id}")
    public Result<CertTransferRecordVO> getTransferDetail(@PathVariable Long id) {
        CertTransferRecordVO vo = certificateService.getTransferDetail(id);
        return Result.success(vo);
    }

    @Operation(summary = "离院记录列表")
    @GetMapping("/leave/page")
    public Result<PageResult<CertLeaveRecordVO>> getLeavePage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "陪护证ID") @RequestParam(required = false) Long certId,
            @Parameter(description = "患者ID") @RequestParam(required = false) Long patientId,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "离院状态 1-离院中 2-已返回 3-已失效") @RequestParam(required = false) Integer leaveStatus,
            @Parameter(description = "陪护人姓名") @RequestParam(required = false) String personName) {
        PageResult<CertLeaveRecordVO> page = certificateService.getLeavePage(current, size,
                certId, patientId, wardId, leaveStatus, personName);
        return Result.success(page);
    }

    @Operation(summary = "离院记录详情")
    @GetMapping("/leave/{id}")
    public Result<CertLeaveRecordVO> getLeaveDetail(@PathVariable Long id) {
        CertLeaveRecordVO vo = certificateService.getLeaveDetail(id);
        return Result.success(vo);
    }
}
