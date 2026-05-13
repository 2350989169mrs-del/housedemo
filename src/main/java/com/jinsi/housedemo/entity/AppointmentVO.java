package com.jinsi.housedemo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentVO {
    private Integer id;
    private Integer houseId;
    private Integer clientUserId;
    private Integer agentUserId;
    private LocalDateTime appointmentTime;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联信息（方便前端展示）
    private String houseCover;        // 房源封面
    private String houseLocation;     // 房源地址
    private String communityName;     // 小区名称
    private BigDecimal rent;          // 租金
    private String clientName;        // 预约人昵称
    private String clientPhone;       // 预约人电话（需通过 user_info 表，可选）
    private String agentName;         // 经纪人姓名（确认后才有）
}
