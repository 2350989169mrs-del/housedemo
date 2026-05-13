package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_admin_info")
public class AdminInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private String realName;
    
    private String idCard;
    
    private String department;
}
