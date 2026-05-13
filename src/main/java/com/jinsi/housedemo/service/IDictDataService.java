package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.DictData;
import com.jinsi.housedemo.entity.DictDataVO;

import java.util.List;
import java.util.Map;

public interface IDictDataService extends IService<DictData> {
    Map<String, List<DictDataVO>> getBatchDictByTypes(List<String> types);
}
