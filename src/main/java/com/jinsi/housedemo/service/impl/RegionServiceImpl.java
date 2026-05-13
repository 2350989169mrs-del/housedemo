package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.Region;
import com.jinsi.housedemo.mapper.RegionMapper;
import com.jinsi.housedemo.service.IRegionService;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements IRegionService {
}
