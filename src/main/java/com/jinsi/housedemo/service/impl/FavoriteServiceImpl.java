package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.*;
import com.jinsi.housedemo.mapper.CommunityMapper;
import com.jinsi.housedemo.mapper.FavoriteMapper;
import com.jinsi.housedemo.mapper.HouseMapper;
import com.jinsi.housedemo.mapper.RegionMapper;
import com.jinsi.housedemo.service.IFavoriteService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements IFavoriteService {
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    @Transactional
    public void addFavorite(Integer userId, Integer houseId) {
        // 校验房源存在且可收藏（上架且审核通过）
        House house = houseMapper.selectById(houseId);
        if (house == null || house.getSalesStatus() != 1 || house.getAuditStatus() != 1) {
            throw new MyException(ErrorType.ERROR, "房源不可收藏");
        }
        // 防止重复收藏
        if (this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getHouseId, houseId)) > 0) {
            throw new MyException(ErrorType.INSERT_ERROR, "已收藏过该房源");
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setHouseId(houseId);
        favorite.setCreateTime(LocalDateTime.now());
        this.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Integer userId, Integer houseId) {
        boolean removed = this.remove(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getHouseId, houseId));
        if (!removed) {
            throw new MyException(ErrorType.UPDATE_ERROR, "未收藏该房源");
        }
    }

    @Override
    public List<FavoriteVO> listMyFavorites(Integer userId) {
        // 1. 查收藏记录，按时间倒序
        List<Favorite> favList = this.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime));
        if (favList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 批量查房源
        Set<Integer> houseIds = favList.stream().map(Favorite::getHouseId).collect(Collectors.toSet());
        List<House> houses = houseMapper.selectBatchIds(houseIds);
        Map<Integer, House> houseMap = houses.stream().collect(Collectors.toMap(House::getId, h -> h));

        // 3. 批量查小区（用于正常房源）
        Set<Integer> commIds = houses.stream()
                .map(House::getCommunityId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, Community> commMap = Collections.emptyMap();
        if (!commIds.isEmpty()) {
            List<Community> communities = communityMapper.selectBatchIds(commIds);
            commMap = communities.stream().collect(Collectors.toMap(Community::getId, c -> c));
        }

        // 4. 批量查区域（用于正常房源）
        Set<Integer> regionIds = commMap.values().stream()
                .map(Community::getRegionId)
                .collect(Collectors.toSet());
        Map<Integer, String> regionNameMap = Collections.emptyMap();
        if (!regionIds.isEmpty()) {
            List<Region> regions = regionMapper.selectBatchIds(regionIds);
            regionNameMap = regions.stream().collect(Collectors.toMap(Region::getId, Region::getRegionName));
        }

        // 5. 组装VO
        List<FavoriteVO> voList = new ArrayList<>();
        for (Favorite fav : favList) {
            House house = houseMap.get(fav.getHouseId());
            FavoriteVO vo = new FavoriteVO();
            vo.setFavoriteId(fav.getId());
            vo.setCreateTime(fav.getCreateTime());

            if (house != null) {
                vo.setHouseId(house.getId());
                vo.setCoverImage(house.getCoverImage());
                vo.setLocation(house.getLocation());
                vo.setRent(house.getRent());
                vo.setLayout(house.getLayout());
                vo.setRentalMethod(house.getRentalMethod());
                vo.setArea(house.getArea());

                // 判断状态
                if (house.getSalesStatus() == 1 && house.getAuditStatus() == 1) {
                    vo.setHouseStatus(1); // 正常
                } else {
                    vo.setHouseStatus(0); // 已下架或未审核通过
                }

                Community comm = commMap.get(house.getCommunityId());
                if (comm != null) {
                    vo.setCommunityName(comm.getName());
                    vo.setRegionName(regionNameMap.getOrDefault(comm.getRegionId(), ""));
                }
            } else {
                // 房源已被物理删除
                vo.setHouseId(fav.getHouseId());
                vo.setCoverImage("");              // 前端用默认图
                vo.setLocation("房源已失效");
                vo.setHouseStatus(-1);
            }
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public boolean isFavorite(Integer userId, Integer houseId) {
        if (userId == null) return false;
        return this.count(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getHouseId, houseId)) > 0;
    }
}
