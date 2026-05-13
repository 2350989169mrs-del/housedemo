package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.AdminInfo;

public interface IAdminInfoService extends IService<AdminInfo> {
    void createAdmin(Integer currentUserId, String account, String password, String realName);
    void updateAdminStatus(Integer currentUserId, Integer adminUserId, Integer status);
}
