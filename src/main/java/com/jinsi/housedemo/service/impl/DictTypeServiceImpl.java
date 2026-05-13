package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.DictType;
import com.jinsi.housedemo.mapper.DictTypeMapper;
import com.jinsi.housedemo.service.IDictTypeService;
import org.springframework.stereotype.Service;

@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements IDictTypeService {
}
