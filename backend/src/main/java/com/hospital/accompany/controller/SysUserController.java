package com.hospital.accompany.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.accompany.common.Result;
import com.hospital.accompany.entity.SysUser;
import com.hospital.accompany.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "系统用户")
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String userName = params.get("userName");
        String password = params.get("password");

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, userName)
                .eq(SysUser::getStatus, 1);
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error(401, "用户不存在或已禁用");
        }
        if (!password.equals(user.getPassword())) {
            return Result.error(401, "密码错误");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("userName", user.getUserName());
        result.put("realName", user.getRealName());
        result.put("roleType", user.getRoleType());
        result.put("wardId", user.getWardId());
        result.put("phone", user.getPhone());
        result.put("token", "mock-token-" + user.getId());

        return Result.success("登录成功", result);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public Result<SysUser> getUserInfo(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        user.setPassword(null);
        return Result.success(user);
    }
}
