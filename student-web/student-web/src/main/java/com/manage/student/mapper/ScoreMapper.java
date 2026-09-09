package com.manage.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manage.student.entity.Score;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {

    @Insert("INSERT INTO score (sid, cid, term, usual, `final`, total) VALUES (#{sid}, #{cid}, #{term}, #{usual}, #{finalScore}, #{total})")
    int insertScore(Score score);

    @Update("UPDATE score SET usual = #{usual}, `final` = #{finalScore}, total = #{total} WHERE sid = #{sid} AND cid = #{cid} AND term = #{term}")
    int updateScore(Score score);
}