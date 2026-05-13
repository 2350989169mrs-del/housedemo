package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_house")
public class House {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer communityId;
    
    private String location;
    
    private String building;
    
    private String unit;
    
    private String roomNumber;
    
    private Integer rentalMethod;
    
    private String leaseTerm;
    
    private BigDecimal rent;
    
    private Integer floor;
    
    private Integer decoration;
    
    private Integer elevatorType;
    
    private Integer layout;
    
    private Integer houseType;
    
    private Integer orientation;
    
    private BigDecimal area;
    
    private String facilities;
    
    private LocalDateTime listingTime;
    
    private Integer publisherId;
    
    private String coverImage;
    
    private String description;
    
    private Integer salesStatus;

    private Integer auditStatus;     // 审核状态 0待审核 1通过 2拒绝
    private Integer auditorId;       // 审核经纪人ID

    private LocalDateTime auditTime; // 审核时间
    private String auditRemark;      // 审核意见
}
