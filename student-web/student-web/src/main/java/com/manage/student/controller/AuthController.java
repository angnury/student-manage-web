package com.manage.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manage.student.common.Result;
import com.manage.student.dto.LoginDTO;
import com.manage.student.entity.SysUser;
import com.manage.student.mapper.SysUserMapper;
import com.manage.student.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证接口。
 * <p>
 * 返回 HTTP 状态码的约定：
 * <ul>
 *     <li>认证失败（账号密码错误 / token 无效）返回 401，前端拦截器据此统一登出；</li>
 *     <li>账号被禁用返回 403；</li>
 *     <li>业务校验失败（参数缺失等）返回 200 + 业务错误码。</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /** 登录，成功返回 JWT 与角色 */
    @PostMapping("/login")
    public ResponseEntity<Result<?>> login(@RequestBody LoginDTO dto) {
        if (dto == null || dto.getUsername() == null || dto.getPassword() == null) {
            return ResponseEntity.ok(Result.error(400, "用户名和密码不能为空"));
        }

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        // 用户不存在与密码错误返回同一提示，避免被枚举出系统里存在哪些账号
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error(401, "用户名或密码错误"));
        }

        // status: 1 启用 / 0 禁用（实体里映射为 String，对应 TINYINT 列）
        if (!"1".equals(user.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Result.error(403, "账号已被禁用，请联系管理员"));
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("role", user.getRole());

        return ResponseEntity.ok(Result.success(data));
    }
}
