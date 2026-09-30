package com.manage.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manage.student.common.BizException;
import com.manage.student.common.RequireRole;
import com.manage.student.common.Result;
import com.manage.student.entity.Course;
import com.manage.student.entity.Score;
import com.manage.student.entity.Student;
import com.manage.student.mapper.CourseMapper;
import com.manage.student.mapper.ScoreMapper;
import com.manage.student.mapper.StudentMapper;
import com.manage.student.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩管理接口（教师 / 管理员均可访问）。
 * <p>
 * 这里只做参数接收与结果返回，成绩计算等业务规则在 {@link ScoreService}。
 */
@RestController
@RequestMapping("/api/teacher")
@RequireRole({"TEACHER", "ADMIN"})
public class ScoreController {

    /** 单次查询返回的学生上限，避免无条件全表返回 */
    private static final int MAX_RESULT = 200;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private CourseMapper courseMapper;

    /** 课程列表，录入成绩时下拉选择 */
    @GetMapping("/courses")
    public Result<?> listCourses() {
        return Result.success(courseMapper.selectList(
                new LambdaQueryWrapper<Course>().orderByAsc(Course::getCid)));
    }

    /**
     * 按条件查询学生及其成绩。
     * 支持学号 / 姓名 / 班级模糊查询，并可选按课程筛选（只返回有该课程成绩的学生）。
     */
    @GetMapping("/students")
    public Result<?> queryStudents(
            @RequestParam(required = false) String sid,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String cid) {

        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(sid), Student::getSid, sid);
        wrapper.like(StringUtils.hasText(name), Student::getSname, name);
        wrapper.like(StringUtils.hasText(className), Student::getSclass, className);
        wrapper.orderByAsc(Student::getSid);
        wrapper.last("LIMIT " + MAX_RESULT);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Student student : studentMapper.selectList(wrapper)) {
            List<Map<String, Object>> scores = scoreMapper.selectScoresByStudent(student.getSid(), cid);
            // 按课程筛选时，没有该课程成绩的学生不展示
            if (StringUtils.hasText(cid) && scores.isEmpty()) {
                continue;
            }
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("student", student);
            item.put("scores", scores);
            result.add(item);
        }
        return Result.success(result);
    }

    /**
     * 查询单个学生的成绩。
     * 学员不存在时返回业务码 404，前端据此弹出「不存在该学员」提示。
     */
    @GetMapping("/students/{sid}/scores")
    public Result<?> getScoresByStudent(@PathVariable String sid) {
        Student student = studentMapper.selectById(sid);
        if (student == null) {
            throw new BizException(404, "不存在该学员");
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("student", student);
        data.put("scores", scoreMapper.selectScoresByStudent(sid, null));
        return Result.success(data);
    }

    /** 录入 / 更新成绩，总分由后端统一计算 */
    @PostMapping("/scores")
    public Result<?> saveOrUpdateScore(@RequestBody Score score) {
        boolean ok = scoreService.saveOrUpdateScore(score);
        return ok ? Result.success("保存成功") : Result.error("保存失败");
    }
}
