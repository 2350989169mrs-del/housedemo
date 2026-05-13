package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.User;
import com.jinsi.housedemo.entity.UserCenterVO;

public interface IUserService extends IService<User> {
    void register(String account, String password, String name);
    UserCenterVO getUserCenter(Integer userId);
}
