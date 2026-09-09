package com.manage.student.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("score")
public class Score {

    @TableField("sid")
    private String sid;

    @TableField("cid")
    private String cid;

    @TableField("term")
    private String term;

    @TableField("usual")
    private Integer usual;

    @TableField("final")
    private Integer finalScore;

    @TableField("total")
    private Integer total;
}