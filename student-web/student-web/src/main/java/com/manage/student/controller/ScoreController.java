package com.manage.student.controller;

import com.manage.student.common.RequireRole;
import com.manage.student.common.Result;
import com.manage.student.entity.Score;
import com.manage.student.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 成绩管理接口（教师 / 管理员）。
 * <p>
 * 这里只做参数接收与结果返回，成绩计算等业务规则在 {@link ScoreService}。
 */
@RestController
@RequestMapping("/api/teacher")
@RequireRole({"TEACHER", "ADMIN"})
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    /** 查询某个学生的所有成绩 */
    @GetMapping("/students/{sid}/scores")
    public Result<?> getScoresByStudent(@PathVariable String sid) {
        return Result.success(scoreService.listByStudent(sid));
    }

    /** 录入 / 更新成绩，总分由后端统一计算 */
    @PostMapping("/scores")
    public Result<?> saveOrUpdateScore(@RequestBody Score score) {
        boolean ok = scoreService.saveOrUpdateScore(score);
        return ok ? Result.success("保存成功") : Result.error("保存失败");
    }
}
