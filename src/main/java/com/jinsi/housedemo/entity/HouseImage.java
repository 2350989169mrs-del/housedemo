package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_house_image")
public class HouseImage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer houseId;
    
    private String url;
    
    private Integer type;
    
    private Integer sort;
}
