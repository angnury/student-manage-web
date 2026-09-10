package com.manage.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manage.student.common.Result;
import com.manage.student.entity.Student;
import com.manage.student.entity.SysUser;
import com.manage.student.mapper.StudentMapper;
import com.manage.student.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 分页查询（已存在）
    @GetMapping
    public Result<?> listStudents(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String className) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(sid), Student::getSid, sid);
        wrapper.like(StringUtils.hasText(name), Student::getSname, name);
        wrapper.like(StringUtils.hasText(className), Student::getSclass, className);
        wrapper.orderByAsc(Student::getSid);
        Page<Student> pageResult = studentMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(pageResult);
    }

    // 新增
    @PostMapping
    @Transactional
    public Result<?> addStudent(@RequestBody Student student) {
        // 1. 检查学号是否已存在
        if (studentMapper.selectById(student.getSid()) != null) {
            return Result.error("学号已存在");
        }
        // 2. 插入 student 表
        int rows = studentMapper.insert(student);
        if (rows == 0) {
            return Result.error("新增学生失败");
        }
        // 3. 创建系统账号（默认密码 123456）
        SysUser user = new SysUser();
        user.setUsername(student.getSid());   // ← 这里改成 getSid()
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRole("STUDENT");
        user.setStatus("1");
        sysUserMapper.insert(user);
        return Result.success("新增成功");
    }

    // 修改
    @PutMapping("/{sid}")
    public Result<?> updateStudent(@PathVariable String sid, @RequestBody Student student) {
        student.setSid(sid);
        int rows = studentMapper.updateById(student);
        return rows > 0 ? Result.success("修改成功") : Result.error("修改失败");
    }

    // 删除
    @DeleteMapping("/{sid}")
    @Transactional
    public Result<?> deleteStudent(@PathVariable String sid) {
        int rows = studentMapper.deleteById(sid);
        if (rows == 0) {
            return Result.error("删除失败");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, sid);
        sysUserMapper.delete(wrapper);
        return Result.success("删除成功");
    }
}