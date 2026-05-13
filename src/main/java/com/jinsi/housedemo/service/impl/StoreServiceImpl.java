package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.Store;
import com.jinsi.housedemo.mapper.StoreMapper;
import com.jinsi.housedemo.service.IStoreService;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements IStoreService {
}
