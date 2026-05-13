package com.jinsi.housedemo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HouseQueryDTO {
    // ========== 区域筛选（通过小区表关联） ==========
    private Long regionId;           // 区域ID（区/县）

    // ========== 地图/位置筛选（通过小区表关联经纬度） ==========
    private BigDecimal centerLng;    // 中心经度（用于附近房源筛选）
    private BigDecimal centerLat;    // 中心纬度
    private BigDecimal radius;       // 搜索半径（公里）

    // ========== 租金范围 ==========
    private BigDecimal rentMin;      // 最低租金
    private BigDecimal rentMax;      // 最高租金

    // ========== 租赁方式 ==========
    private Integer rentalMethod;    // 1=整租, 2=合租

    // ========== 户型（支持多选） ==========
    private List<Integer> layouts;   // 字典值，允许同时选 一居+两居

    // ========== 朝向（支持多选） ==========
    private List<Integer> orientations;

    // ========== 装修 ==========
    private Integer decoration;

    // ========== 设施（多选，传 1,3,6） ==========
    private List<Integer> facilities; // 或者用 String，看你存储方式

    // ========== 面积范围 ==========
    private BigDecimal areaMin;
    private BigDecimal areaMax;

    // ========== 电梯 ==========
    private Integer elevatorType;      // 1=有, 0=无

    // ========== 房龄（通过小区表筛选） ==========
    private String buildingAgeMin;      // 小区最老建筑年份，如 "2000"
    private String buildingAgeMax;     // 小区最新建筑年份

    // ========== 关键词 ==========
    private String keyword;           // 模糊搜索小区名称、地址、房屋描述

    private Integer floorMin;            // 新增
    private Integer floorMax;            // 新增
    private LocalDateTime listingTimeStart; // 新增
    private LocalDateTime listingTimeEnd;   // 新增

    // 后台可能用到的状态，前端一般固定传
    private Integer salesStatus;         // 1上架 0下架
    private Integer auditStatus;         // 0待审 1通过 2拒绝

    // ========== 排序 ==========
    private String sortField;         // rent / area / listing_time
    private Boolean sortAsc;          // true=升序, false=降序 默认升序

    // ========== 分页 ==========
    private Integer pageNum = 1;      // 当前页码
    private Integer pageSize = 10;    // 每页条数
}
