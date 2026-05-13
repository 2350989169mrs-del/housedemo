package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.AdminInfo;
import com.jinsi.housedemo.entity.User;
import com.jinsi.housedemo.mapper.AdminInfoMapper;
import com.jinsi.housedemo.mapper.UserMapper;
import com.jinsi.housedemo.service.IAdminInfoService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminInfoServiceImpl extends ServiceImpl<AdminInfoMapper, AdminInfo> implements IAdminInfoService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdminInfoMapper adminInfoMapper;

    @Override
    @Transactional
    public void createAdmin(Integer currentUserId, String account, String password, String realName) {
        // 仅超管可操作
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null || currentUser.getUserType() != 5) {
            throw new MyException(ErrorType.ERROR, "仅超级管理员可创建管理员");
        }

        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getAccount, account)) > 0) {
            throw new MyException(ErrorType.INSERT_ERROR, "手机号已被使用");
        }

        User adminUser = new User();
        adminUser.setAccount(account);
        adminUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        adminUser.setName(realName);
        adminUser.setUserType(4);
        adminUser.setAccountStatus(1);
        adminUser.setRoleId(4);
        userMapper.insert(adminUser);

        AdminInfo adminInfo = new AdminInfo();
        adminInfo.setUserId(adminUser.getId());
        adminInfo.setRealName(realName);
        adminInfoMapper.insert(adminInfo);
    }

    @Override
    @Transactional
    public void updateAdminStatus(Integer currentUserId, Integer adminUserId, Integer status) {
        User currentUser = userMapper.selectById(currentUserId);
        if (currentUser == null || currentUser.getUserType() != 5) {
            throw new MyException(ErrorType.ERROR, "仅超级管理员可操作");
        }

        User adminUser = userMapper.selectById(adminUserId);
        if (adminUser == null || adminUser.getUserType() != 4) {
            throw new MyException(ErrorType.UPDATE_ERROR, "该用户不是管理员");
        }

        adminUser.setAccountStatus(status);
        userMapper.updateById(adminUser);
    }
}
