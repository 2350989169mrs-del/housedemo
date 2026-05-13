package com.jinsi.housedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinsi.housedemo.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
