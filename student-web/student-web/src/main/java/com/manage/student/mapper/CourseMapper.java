package com.manage.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manage.student.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
