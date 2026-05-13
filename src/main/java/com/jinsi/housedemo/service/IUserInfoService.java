package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.UserInfo;

import java.time.LocalDate;
import java.util.Date;

public interface IUserInfoService extends IService<UserInfo> {
    public void saveAndUpdateUserType(Integer userId, String realName, String idCard,
                                      Integer gender, LocalDate birthday, String email);
}
