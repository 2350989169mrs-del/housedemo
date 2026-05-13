package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("t_user_info")
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private String realName;
    
    private String idCard;
    
    private Integer gender;
    
    private LocalDate birthday;
    
    private String email;
}
