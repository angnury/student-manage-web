package com.manage.student.controller;

import com.manage.student.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public Result<?> getCurrentUser(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success("当前登录用户: " + username);
    }
}