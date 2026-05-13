package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_role")
public class Role {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String roleName;
    
    private String roleKey;
    
    private String description;
    
    private Integer status;
}
