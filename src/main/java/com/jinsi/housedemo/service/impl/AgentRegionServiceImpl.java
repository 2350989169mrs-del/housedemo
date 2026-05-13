package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.AgentRegion;
import com.jinsi.housedemo.mapper.AgentRegionMapper;
import com.jinsi.housedemo.service.IAgentRegionService;
import org.springframework.stereotype.Service;

@Service
public class AgentRegionServiceImpl extends ServiceImpl<AgentRegionMapper, AgentRegion> implements IAgentRegionService {
}
