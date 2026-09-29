package com.manage.student.dto;

import lombok.Data;

/**
 * 登录请求体。
 * 用 DTO 而不是直接接收实体，避免前端多传字段（如 role）被直接落到数据库。
 */
@Data
public class LoginDTO {

    private String username;

    private String password;
}
