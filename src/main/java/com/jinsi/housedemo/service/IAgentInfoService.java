package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.AgentInfo;

public interface IAgentInfoService extends IService<AgentInfo> {
    void createAgent(Integer currentUserId, String account, String password, String realName,
                     String idCard, Integer storeId, String phone);
    void updateAgentStatus(Integer currentUserId, Integer agentUserId, Integer status);
}
