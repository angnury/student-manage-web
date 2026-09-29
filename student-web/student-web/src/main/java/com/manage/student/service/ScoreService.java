package com.manage.student.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.manage.student.common.BizException;
import com.manage.student.entity.Score;
import com.manage.student.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 成绩业务逻辑。
 * <p>
 * 分层约定：Controller 只负责参数接收与结果返回，业务规则（如总分计算公式、
 * 存在则更新否则插入）全部放在 Service，便于复用与单元测试。
 */
@Service
public class ScoreService {

    /** 平时分权重 */
    private static final double USUAL_WEIGHT = 0.4;
    /** 期末分权重 */
    private static final double FINAL_WEIGHT = 0.6;

    @Autowired
    private ScoreMapper scoreMapper;

    /** 查询某个学生的全部成绩。 */
    public List<Score> listByStudent(String sid) {
        return scoreMapper.selectList(new LambdaQueryWrapper<Score>().eq(Score::getSid, sid));
    }

    /**
     * 录入或更新成绩。
     * <p>
     * 总分必须由后端按统一规则计算，不接受前端传入的 total，防止改分。
     * 即使前端传了 total，也会在下面被覆盖。
     *
     * @return 是否写入成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateScore(Score score) {
        if (score.getSid() == null || score.getCid() == null || score.getTerm() == null) {
            throw new BizException("学号、课程编号、学期不能为空");
        }
        if (score.getUsual() == null || score.getFinalScore() == null) {
            throw new BizException("平时分和期末分不能为空");
        }

        // 成绩规则下沉到 Service：平时分 x 0.4 + 期末分 x 0.6，四舍五入取整
        int total = (int) Math.round(score.getUsual() * USUAL_WEIGHT + score.getFinalScore() * FINAL_WEIGHT);
        score.setTotal(total);

        // score 表联合主键是 (sid, cid, term)，同课不同学期允许共存
        Score existing = scoreMapper.selectOne(new LambdaQueryWrapper<Score>()
                .eq(Score::getSid, score.getSid())
                .eq(Score::getCid, score.getCid())
                .eq(Score::getTerm, score.getTerm()));

        return existing != null
                ? scoreMapper.updateScore(score) > 0
                : scoreMapper.insertScore(score) > 0;
    }
}
