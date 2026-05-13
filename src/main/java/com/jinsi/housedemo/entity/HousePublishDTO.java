package com.jinsi.housedemo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HousePublishDTO {
    private Integer communityId;      // 小区ID
    private String location;          // 对外展示地址
    private String building;          // 楼栋号（内部）
    private String unit;              // 单元（内部）
    private String roomNumber;        // 房间号（内部）
    private Integer rentalMethod;     // 租赁方式 1=整租,2=合租
    private String leaseTerm;         // 租期要求
    private BigDecimal rent;          // 月租金
    private Integer floor;            // 楼层
    private Integer decoration;       // 装修方式（字典）
    private Integer elevatorType;     // 电梯 1=有,0=无
    private Integer layout;           // 户型（字典）
    private Integer houseType;        // 房屋类型（字典）
    private Integer orientation;      // 朝向（字典）
    private BigDecimal area;          // 面积
    private String facilities;        // 设施（逗号分隔字典值）
    private String coverImage;        // 封面图片URL
    private String description;       // 房屋描述
    private List<String> imageUrls;   // 户型图/预览图URL列表（前端可传多个）
}
