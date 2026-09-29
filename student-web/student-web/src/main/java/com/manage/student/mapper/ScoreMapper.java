package com.manage.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manage.student.entity.Score;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    @Insert("INSERT INTO score (sid, cid, term, usual, `final`, total) VALUES (#{sid}, #{cid}, #{term}, #{usual}, #{finalScore}, #{total})")
    int insertScore(Score score);

    @Update("UPDATE score SET usual = #{usual}, `final` = #{finalScore}, total = #{total} WHERE sid = #{sid} AND cid = #{cid} AND term = #{term}")
    int updateScore(Score score);

    /**
     * 成绩分布：按分数段分组统计人数。
     * 让数据库做聚合，只返回 5 行结果，避免把全表捞进内存再在 Java 里循环计数。
     */
    @Select("""
            SELECT CASE
                     WHEN total >= 90 THEN '优秀(>=90)'
                     WHEN total >= 80 THEN '良好(80-89)'
                     WHEN total >= 70 THEN '中等(70-79)'
                     WHEN total >= 60 THEN '及格(60-69)'
                     ELSE '不及格(<60)'
                   END AS segment,
                   COUNT(*) AS num
            FROM score
            WHERE total IS NOT NULL
            GROUP BY segment
            """)
    List<Map<String, Object>> selectScoreDistribution();

    /**
     * 班级平均分与及格率：JOIN student 后按班级聚合。
     * 及格率 = 及格人数 / 总人数，用 CASE WHEN 在 SQL 里算好，前端直接展示。
     */
    @Select("""
            SELECT s.sclass                                        AS className,
                   ROUND(AVG(sc.total), 1)                         AS avgScore,
                   SUM(CASE WHEN sc.total >= 60 THEN 1 ELSE 0 END) AS passCount,
                   COUNT(*)                                        AS totalCount,
                   CONCAT(ROUND(SUM(CASE WHEN sc.total >= 60 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1), '%') AS passRate
            FROM score sc
            JOIN student s ON s.sid = sc.sid
            WHERE sc.total IS NOT NULL
            GROUP BY s.sclass
            ORDER BY avgScore DESC
            """)
    List<Map<String, Object>> selectClassAverage();
}
