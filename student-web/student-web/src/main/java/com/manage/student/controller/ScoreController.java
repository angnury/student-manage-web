package com.manage.student.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manage.student.common.Result;
import com.manage.student.entity.Score;
import com.manage.student.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher")
public class ScoreController {

    @Autowired
    private ScoreMapper scoreMapper;

    // 1. 查询某个学生的所有成绩（根据学号）
    @GetMapping("/students/{sid}/scores")
    public Result<?> getScoresByStudent(@PathVariable String sid) {
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getSid, sid);
        return Result.success(scoreMapper.selectList(wrapper));
    }

    // 2. 录入/更新成绩（教师录入）
    @PostMapping("/scores")
    public Result<?> saveOrUpdateScore(@RequestBody Score score) {
        if (score.getUsual() == null || score.getFinalScore() == null) {
            return Result.error("平时分和期末分不能为空");
        }
        int total = (int) Math.round(score.getUsual() * 0.4 + score.getFinalScore() * 0.6);
        score.setTotal(total);

        // 检查是否存在
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getSid, score.getSid())
                .eq(Score::getCid, score.getCid())
                .eq(Score::getTerm, score.getTerm());
        Score existing = scoreMapper.selectOne(wrapper);

        if (existing != null) {
            scoreMapper.updateScore(score);
            return Result.success("成绩更新成功");
        } else {
            scoreMapper.insertScore(score);
            return Result.success("成绩录入成功");
        }
    }
}