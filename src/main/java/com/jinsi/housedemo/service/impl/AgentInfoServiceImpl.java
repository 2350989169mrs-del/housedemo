package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.AgentInfo;
import com.jinsi.housedemo.entity.User;
import com.jinsi.housedemo.mapper.AgentInfoMapper;
import com.jinsi.housedemo.mapper.UserMapper;
import com.jinsi.housedemo.service.IAgentInfoService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentInfoServiceImpl extends ServiceImpl<AgentInfoMapper, AgentInfo> implements IAgentInfoService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AgentInfoMapper agentInfoMapper;

    @Override
    @Transactional
    public void createAgent(Integer currentUserId, String account, String password,
                            String realName, String idCard, Integer storeId, String phone) {
        // 1. 权限校验：必须是管理员或超管
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null || (currentUser.getUserType() != 4 && currentUser.getUserType() != 5)) {
            throw new MyException(ErrorType.ERROR, "无权限，仅管理员或超级管理员可创建经纪人");
        }

        // 2. 手机号唯一性
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getAccount, account)) > 0) {
            throw new MyException(ErrorType.INSERT_ERROR, "手机号已被使用");
        }

        // 3. 创建用户
        User agentUser = new User();
        agentUser.setAccount(account);
        agentUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        agentUser.setName(realName);
        agentUser.setUserType(3);          // 经纪人
        agentUser.setAccountStatus(1);     // 正常
        agentUser.setRoleId(3);            // 经纪人角色
        userMapper.insert(agentUser);

        // 4. 创建经纪人扩展信息
        AgentInfo agentInfo = new AgentInfo();
        agentInfo.setUserId(agentUser.getId());
        agentInfo.setRealName(realName);
        agentInfo.setIdCard(idCard);
        agentInfo.setStoreId(storeId);
        agentInfo.setPhone(phone);
        agentInfoMapper.insert(agentInfo);
    }

    @Override
    @Transactional
    public void updateAgentStatus(Integer currentUserId, Integer agentUserId, Integer status) {
        // 1. 权限校验
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null || (currentUser.getUserType() != 4 && currentUser.getUserType() != 5)) {
            throw new MyException(ErrorType.ERROR, "无权限，仅管理员或超级管理员可操作");
        }

        // 2. 确认目标用户是经纪人
        User agentUser = userMapper.selectById(agentUserId);
        if (agentUser == null || agentUser.getUserType() != 3) {
            throw new MyException(ErrorType.UPDATE_ERROR, "该用户不是经纪人");
        }

        // 3. 更新状态
        agentUser.setAccountStatus(status);
        userMapper.updateById(agentUser);
    }
}
