package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.HouseImage;
import com.jinsi.housedemo.mapper.HouseImageMapper;
import com.jinsi.housedemo.service.IHouseImageService;
import org.springframework.stereotype.Service;

@Service
public class HouseImageServiceImpl extends ServiceImpl<HouseImageMapper, HouseImage> implements IHouseImageService {
}
