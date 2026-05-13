package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_dict_type")
public class DictType {
    @TableId(type = IdType.AUTO)
    private Integer dictId;
    
    private String dictName;
    
    private String dictType;
    
    private Integer status;
    
    private String remark;
}
