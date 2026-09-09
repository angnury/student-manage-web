package com.manage.student.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("student")   // 指定表名
public class Student {

    @TableId               // 指定主键
    private String sid;        // 学号

    private String sname;      // 姓名（对应数据库 sname）

    private String sex;        // 性别（对应数据库 sex）

    private String sclass;     // 班级（对应数据库 sclass）

    private LocalDate birthday; // 出生日期

    private String jg;          // 籍贯

    private String phone;       // 手机号

    private LocalDate enterTime; // 入学时间（对应 enter_time）

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}