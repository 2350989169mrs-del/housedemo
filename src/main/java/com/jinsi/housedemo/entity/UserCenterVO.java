package com.jinsi.housedemo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserCenterVO {
    // 基础用户信息
    private Integer userId;
    private String avatar;
    private String name;
    private String account;
    private Integer userType;
    private Integer accountStatus;
    private LocalDateTime createTime;

    // 扩展信息（会员/经纪人/管理员 都有可能有）
    private String realName;
    private String idCard;
    private String email;
    private Integer gender;
    private LocalDate birthday;

    // 经纪人特有
    private String phone;           // 经纪人联系电话
    private Integer storeId;
    private String storeName;       // 门店名称
    private String licenseNumber;
    private Integer serviceYears;
    private String introduction;

    // 管理员特有
    private String department;

    // 统计信息（可选）
    private long publishCount;      // 发布的房源数
    private long favoriteCount;     // 收藏数
    private long appointmentCount;  // 预约数（会员）/处理预约数（经纪人）
}
