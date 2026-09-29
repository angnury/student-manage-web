package com.manage.student.controller;

import com.manage.student.common.Result;
import com.manage.student.common.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前登录用户信息。
 * 任何已登录角色都能访问（不加 @RequireRole）。
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /** 当前登录人信息由 AuthInterceptor 写入 UserContext，这里直接读，不必透传 request */
    @GetMapping("/me")
    public Result<?> getCurrentUser() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", UserContext.getUsername());
        data.put("role", UserContext.getRole());
        return Result.success(data);
    }
}
