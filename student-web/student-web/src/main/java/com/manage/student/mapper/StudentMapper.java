package com.manage.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manage.student.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /** 去重统计班级数量，供工作台概览使用 */
    @Select("SELECT COUNT(DISTINCT sclass) FROM student WHERE sclass IS NOT NULL AND sclass <> ''")
    Long countDistinctClass();
}
