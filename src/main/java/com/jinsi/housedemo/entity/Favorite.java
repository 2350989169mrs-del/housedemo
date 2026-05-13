package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_favorite")
public class Favorite {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer userId;
    
    private Integer houseId;
    
    private LocalDateTime createTime;
}
