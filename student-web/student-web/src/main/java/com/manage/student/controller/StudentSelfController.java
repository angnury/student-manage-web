package com.manage.student.controller;

import com.manage.student.common.BizException;
import com.manage.student.common.RequireRole;
import com.manage.student.common.Result;
import com.manage.student.common.UserContext;
import com.manage.student.entity.Student;
import com.manage.student.mapper.ScoreMapper;
import com.manage.student.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学生自助接口。
 * <p>
 * 学生的登录账号就是学号，直接从 {@link UserContext} 取，不接受前端传 sid，
 * 这样即使前端乱传也查不到别人的数据（水平越权防护）。
 */
@RestController
@RequestMapping("/api/student/my")
@RequireRole("STUDENT")
public class StudentSelfController {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    /** 我的个人信息 */
    @GetMapping("/info")
    public Result<?> myInfo() {
        Student student = studentMapper.selectById(UserContext.getUsername());
        if (student == null) {
            throw new BizException(404, "未找到你的学籍信息，请联系管理员");
        }
        return Result.success(student);
    }

    /** 我的全部成绩 */
    @GetMapping("/scores")
    public Result<?> myScores() {
        return Result.success(scoreMapper.selectScoresByStudent(UserContext.getUsername(), null));
    }
}
