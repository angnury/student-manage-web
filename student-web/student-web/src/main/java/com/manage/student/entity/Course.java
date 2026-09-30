package com.manage.student.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 课程表 course。
 * 成绩录入时需要课程下拉选项，因此单独建实体。
 */
@Data
@TableName("course")
public class Course {

    @TableId
    @TableField("cid")
    private String cid;        // 课程编号

    @TableField("cname")
    private String cname;      // 课程名

    @TableField("credit")
    private Integer credit;    // 学分

    @TableField("tid")
    private String tid;        // 授课教师工号
}
