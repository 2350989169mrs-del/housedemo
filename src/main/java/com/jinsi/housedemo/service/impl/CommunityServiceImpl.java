package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.Community;
import com.jinsi.housedemo.mapper.CommunityMapper;
import com.jinsi.housedemo.service.ICommunityService;
import org.springframework.stereotype.Service;

@Service
public class CommunityServiceImpl extends ServiceImpl<CommunityMapper, Community> implements ICommunityService {
}
