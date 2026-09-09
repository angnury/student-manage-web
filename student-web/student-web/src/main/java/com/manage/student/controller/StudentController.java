package com.manage.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manage.student.common.Result;
import com.manage.student.entity.Student;
import com.manage.student.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    @Autowired
    private StudentMapper studentMapper;

    // 分页 + 多条件模糊搜索
    @GetMapping
    public Result<?> listStudents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String className) {

        // 1. 构建查询条件
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(sid), Student::getSid, sid);
        wrapper.like(StringUtils.hasText(name), Student::getSname, name);
        wrapper.like(StringUtils.hasText(className), Student::getSclass, className);
        wrapper.orderByAsc(Student::getSid); // 按学号升序

        // 2. 执行分页查询
        Page<Student> pageResult = studentMapper.selectPage(new Page<>(page, size), wrapper);

        // 3. 返回结果
        return Result.success(pageResult);
    }
}