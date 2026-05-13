package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_agent_region")
public class AgentRegion {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer agentUserId;
    
    private Integer regionId;
    
    private String streetName;
}
