package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_store")
public class Store {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String storeName;
    
    private Integer regionId;
    
    private String address;
    
    private String phone;
    
    private Integer status;
}
