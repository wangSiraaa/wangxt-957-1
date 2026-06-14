package com.hospital.accompany.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.entity.Patient;
import com.hospital.accompany.mapper.PatientMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "患者管理")
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientMapper patientMapper;

    @Operation(summary = "分页查询患者列表")
    @GetMapping("/page")
    public Result<PageResult<Patient>> getPatientPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "病区ID") @RequestParam(required = false) Long wardId,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String patientName,
            @Parameter(description = "住院号") @RequestParam(required = false) String patientNo) {
        Page<Patient> page = new Page<>(current, size);
        IPage<Patient> result = patientMapper.selectPatientPage(page, wardId, patientName, patientNo);
        PageResult<Patient> pageResult = new PageResult<>(result.getTotal(), result.getRecords(),
                result.getCurrent(), result.getSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "获取患者详情")
    @GetMapping("/{id}")
    public Result<Patient> getPatientDetail(@PathVariable Long id) {
        Patient patient = patientMapper.selectById(id);
        return Result.success(patient);
    }
}
