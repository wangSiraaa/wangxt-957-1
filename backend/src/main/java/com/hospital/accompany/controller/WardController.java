package com.hospital.accompany.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.entity.Ward;
import com.hospital.accompany.mapper.WardMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "病区管理")
@RestController
@RequestMapping("/ward")
public class WardController {

    @Autowired
    private WardMapper wardMapper;

    @Operation(summary = "获取所有病区列表")
    @GetMapping("/list")
    public Result<List<Ward>> getWardList() {
        LambdaQueryWrapper<Ward> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ward::getStatus, 1)
                .orderByAsc(Ward::getWardCode);
        List<Ward> list = wardMapper.selectList(wrapper);
        return Result.success(list);
    }

    @Operation(summary = "获取病区详情")
    @GetMapping("/{id}")
    public Result<Ward> getWardDetail(@PathVariable Long id) {
        Ward ward = wardMapper.selectById(id);
        return Result.success(ward);
    }
}
