package com.jinsi.housedemo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FavoriteVO {
    private Integer favoriteId;      // 收藏记录ID
    private Integer houseId;
    private String coverImage;
    private String location;
    private BigDecimal rent;
    private Integer layout;
    private Integer rentalMethod;
    private BigDecimal area;
    private String communityName;
    private String regionName;
    private Integer houseStatus;  // 新增：1=正常, 0=已下架, -1=已删除
    private LocalDateTime createTime; // 收藏时间
}
