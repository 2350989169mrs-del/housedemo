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

    /** 会员查看自己发布的房源 */
    List<House> listByPublisher(Integer userId);
    /** 下架房源 */
    void delist(Integer houseId, Integer userId, Integer userType);
    /** 重新上架 */
    void relist(Integer houseId, Integer userId, Integer userType);
    /** 删除（超管物理删，其他人逻辑删） */
    boolean deleteHouse(Integer houseId, Integer userId, Integer userType);
}