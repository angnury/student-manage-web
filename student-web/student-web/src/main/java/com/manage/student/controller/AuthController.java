package com.manage.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manage.student.common.Result;
import com.manage.student.entity.SysUser;
import com.manage.student.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 改为 PostMapping，符合 RESTful 规范
    @PostMapping("/login")
    public Result<?> login(@RequestParam String username, @RequestParam String password) {
        // 1. 根据用户名查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            return Result.error("用户不存在");
        }

        // 2. BCrypt 密码校验（关键！）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error(401, "密码错误");
        }

        // 3. 密码正确，返回用户信息（后续换成 JWT Token）
        return Result.success(user);
    }
}