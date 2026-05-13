package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("t_community")
public class Community {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer regionId;
    
    private String name;
    
    private String address;
    
    private String buildingAge;
    
    private Integer totalUnits;
    
    private Integer floorCount;
    
    private Integer hasProperty;
    
    private String propertyName;

    private BigDecimal longitude;

    private BigDecimal latitude;
    
    private Integer status;
}
