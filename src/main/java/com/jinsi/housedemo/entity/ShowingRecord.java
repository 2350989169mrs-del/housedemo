package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_showing_record")
public class ShowingRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer appointmentId;
    
    private Integer agentUserId;
    
    private LocalDateTime showingTime;
    
    private Integer clientIntention;
    
    private Integer clientSatisfaction;
    
    private String remark;
    
    private LocalDateTime createTime;
}
