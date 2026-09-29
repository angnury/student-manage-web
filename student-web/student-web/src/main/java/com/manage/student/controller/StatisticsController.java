package com.manage.student.controller;

import com.manage.student.common.RequireRole;
import com.manage.student.common.Result;
import com.manage.student.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表接口（仅管理员可访问）。
 * <p>
 * 聚合逻辑全部下沉到 SQL（见 {@link ScoreMapper}），这里只做结果格式转换。
 */
@RestController
@RequestMapping("/api/admin/statistics")
@RequireRole("ADMIN")
public class StatisticsController {

    /** 分数段展示顺序，前端柱状图按此顺序出柱子 */
    private static final String[] SEGMENTS = {"优秀(>=90)", "良好(80-89)", "中等(70-79)", "及格(60-69)", "不及格(<60)"};

    @Autowired
    private ScoreMapper scoreMapper;

    /** 成绩分布：各分数段人数 */
    @GetMapping("/score-distribution")
    public Result<?> getScoreDistribution() {
        List<Map<String, Object>> rows = scoreMapper.selectScoreDistribution();

        // 用 LinkedHashMap 固定顺序，并把没有数据的分数段补 0（否则柱状图会缺柱子）
        Map<String, Object> result = new LinkedHashMap<>();
        for (String segment : SEGMENTS) {
            result.put(segment, 0L);
        }
        for (Map<String, Object> row : rows) {
            result.put(String.valueOf(row.get("segment")), row.get("num"));
        }
        return Result.success(result);
    }

    /** 班级平均分 / 及格率 */
    @GetMapping("/class-average")
    public Result<?> getClassAverage() {
        return Result.success(scoreMapper.selectClassAverage());
    }
}
