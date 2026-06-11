package com.hospital.accompany.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.accompany.common.PageResult;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.entity.Patient;
import com.hospital.accompany.mapper.PatientMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "患者管理")
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientMapper patientMapper;

    @ApiOperation("分页查询患者列表")
    @GetMapping("/page")
    public Result<PageResult<Patient>> getPatientPage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Long current,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") Long size,
            @ApiParam("病区ID") @RequestParam(required = false) Long wardId,
            @ApiParam("患者姓名") @RequestParam(required = false) String patientName,
            @ApiParam("住院号") @RequestParam(required = false) String patientNo) {
        Page<Patient> page = new Page<>(current, size);
        Page<Patient> result = patientMapper.selectPatientPage(page, wardId, patientName, patientNo);
        PageResult<Patient> pageResult = new PageResult<>(result.getTotal(), result.getRecords(),
                result.getCurrent(), result.getSize());
        return Result.success(pageResult);
    }

    @ApiOperation("获取患者详情")
    @GetMapping("/{id}")
    public Result<Patient> getPatientDetail(@PathVariable Long id) {
        Patient patient = patientMapper.selectById(id);
        return Result.success(patient);
    }
}
