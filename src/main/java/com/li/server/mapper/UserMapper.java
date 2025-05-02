package com.li.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
