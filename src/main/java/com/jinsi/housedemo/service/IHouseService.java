package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.*;

import java.util.List;


public interface IHouseService extends IService<House> {
    void publish(Integer userId, HousePublishDTO dto);   // 会员发布房源
    void audit(Integer userId, Integer houseId, Integer auditStatus, String auditRemark); // 经纪人审核

    IPage<HouseListVO> searchHouses(HouseQueryDTO dto, Integer userType);

    HouseDetailVO getDetailById(Integer houseId, Integer userId, Integer userType);
}
