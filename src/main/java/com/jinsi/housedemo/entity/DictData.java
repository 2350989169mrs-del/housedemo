package com.jinsi.housedemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_dict_data")
public class DictData {
    @TableId(type = IdType.AUTO)
    private Integer dataId;
    
    private String dictType;
    
    private String dictLabel;
    
    private String dictValue;
    
    private Integer sort;
    
    private Integer status;
    
    private String remark;
}
