package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.DictData;
import com.jinsi.housedemo.mapper.DictDataMapper;
import com.jinsi.housedemo.entity.DictDataVO;
import com.jinsi.housedemo.service.IDictDataService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements IDictDataService {
    @Override
    public Map<String, List<DictDataVO>> getBatchDictByTypes(List<String> types) {
        if (types == null || types.isEmpty()) {
            return Collections.emptyMap();
        }
        List<DictData> list = this.list(new LambdaQueryWrapper<DictData>()
                .in(DictData::getDictType, types)
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getSort));

        // 按 dictType 分组
        Map<String, List<DictDataVO>> result = new HashMap<>();
        for (String type : types) {
            List<DictDataVO> vos = list.stream()
                    .filter(d -> type.equals(d.getDictType()))
                    .map(d -> new DictDataVO(d.getDictValue(), d.getDictLabel()))
                    .collect(Collectors.toList());
            result.put(type, vos);
        }
        return result;
    }
}
