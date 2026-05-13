package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String avatar;
    
    private String name;
    
    private String password;
    
    private String account;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    private Integer accountStatus;
    
    private Integer roleId;
    
    private Integer userType;
}
