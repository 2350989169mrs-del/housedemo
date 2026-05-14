package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.User;
import com.jinsi.housedemo.entity.UserCenterVO;

/**
 * 用户服务接口
 */
public interface IUserService extends IService<User> {

    /**
     * 注册新用户（默认普通用户 userType=1）
     */
    void register(String account, String password, String name);

    /**
     * 获取用户个人中心数据
     */
    UserCenterVO getUserCenter(Integer userId);

    /**
     * 修改密码（需验证旧密码）
     */
    void changePassword(Integer userId, String oldPassword, String newPassword);

    /**
     * 更新头像和昵称（允许只传其中一个）
     */
    void updateProfile(Integer userId, String avatar, String name);
}