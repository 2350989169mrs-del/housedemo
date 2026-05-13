package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.User;
import com.jinsi.housedemo.entity.UserInfo;
import com.jinsi.housedemo.mapper.UserInfoMapper;
import com.jinsi.housedemo.mapper.UserMapper;
import com.jinsi.housedemo.service.IUserInfoService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    @Transactional
    public void saveAndUpdateUserType(Integer  userId, String realName, String idCard,
                                      Integer gender, LocalDate birthday, String email) {
        // 1. 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new MyException(ErrorType.UPDATE_ERROR, "用户不存在");
        }

        // 2. 查询是否已有扩展信息
        UserInfo userInfo = userInfoMapper.selectOne(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));

        if (userInfo == null) {
            // 首次完善：插入 user_info
            userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setRealName(realName);
            userInfo.setIdCard(idCard);
            userInfo.setGender(gender);
            userInfo.setBirthday(birthday);
            userInfo.setEmail(email);
            userInfoMapper.insert(userInfo);
        } else {
            // 已存在：更新
            userInfo.setRealName(realName);
            userInfo.setIdCard(idCard);
            userInfo.setGender(gender);
            userInfo.setBirthday(birthday);
            userInfo.setEmail(email);
            userInfoMapper.updateById(userInfo);
        }

        // 3. 若当前是普通用户，升级为会员
        if (user.getUserType() == 1) {
            user.setUserType(2);          // 会员
            user.setRoleId(2);           // 会员角色（假设角色表 id=2）
            userMapper.updateById(user);
        }
        // 如果已经是会员，只更新资料，不重复升级
    }
}
