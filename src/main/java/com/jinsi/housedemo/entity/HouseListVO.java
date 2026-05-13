package com.jinsi.housedemo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class HouseListVO {
    private Integer id;
    private String coverImage;
    private BigDecimal rent;
    private BigDecimal area;
    private Integer layout;          // 字典值，前端自己转换
    private Integer rentalMethod;
    private String location;
    private Integer floor;
    private Integer orientation;
    private String facilities;       // 原始逗号分隔，前端可处理成图标
    private LocalDateTime listingTime;

    // 关联信息
    private String communityName;
    private String regionName;

    // 以下为经纪人/管理员可见字段，普通用户返回 null
    private String building;
    private String unit;
    private String roomNumber;
    private Integer auditStatus;
    private Integer publisherId;
}
