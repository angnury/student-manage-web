package com.manage.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manage.student.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    // 继承 BaseMapper 后，默认就有了 insert、selectById、update、delete 等方法
    // 我们暂时不用写任何代码，MyBatis-Plus 会自动帮我们生成 SQL
}