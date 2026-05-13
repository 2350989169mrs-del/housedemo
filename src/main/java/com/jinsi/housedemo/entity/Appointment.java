package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_appointment")
public class Appointment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer houseId;
    
    private Integer clientUserId;
    
    private Integer agentUserId;
    
    private LocalDateTime appointmentTime;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
