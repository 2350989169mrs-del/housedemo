package com.jinsi.housedemo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HouseDetailVO {
    // 房屋基础信息
    private Integer id;
    private String coverImage;
    private BigDecimal rent;
    private BigDecimal area;
    private Integer layout;          // 字典值
    private Integer rentalMethod;
    private String leaseTerm;
    private Integer floor;
    private Integer decoration;
    private Integer elevatorType;
    private Integer orientation;
    private String facilities;
    private String location;
    private String description;
    private LocalDateTime listingTime;

    // 关联信息
    private String communityName;
    private String communityAddress;
    private String buildingAge;
    private String propertyName;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String regionName;

    // 发布者信息（会员）
    private String publisherName;
    private String publisherAvatar;

    // 内部字段（经纪人/管理员可见，普通用户为 null）
    private String building;
    private String unit;
    private String roomNumber;
    private Integer auditStatus;
    private Integer publisherId;

    // 图片列表
    private List<String> layoutImages;   // 户型图
    private List<String> previewImages;  // 预览图/房屋图

    // 当前用户是否收藏
    private Boolean isFavorite;
}
