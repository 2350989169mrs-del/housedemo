package com.jinsi.housedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinsi.housedemo.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
