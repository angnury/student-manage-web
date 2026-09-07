package com.manage.student.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Integer id;               // 用户ID
    private String username;          // 用户名
    private String password;          // 密码（存BCrypt密文）
    private String role;              // 角色: ADMIN, STUDENT, TEACHER
    private String status;            // 状态: 0-禁用, 1-启用
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}