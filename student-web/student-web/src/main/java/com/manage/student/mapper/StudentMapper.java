package com.manage.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manage.student.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
    // 继承 BaseMapper 后，分页查询、增删改查方法都有了
}