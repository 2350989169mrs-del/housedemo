package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.*;
import com.jinsi.housedemo.mapper.*;
import com.jinsi.housedemo.service.IFavoriteService;
import com.jinsi.housedemo.service.IHouseImageService;
import com.jinsi.housedemo.service.IHouseService;
import com.jinsi.housedemo.service.IUserService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements IHouseService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private AgentRegionMapper agentRegionMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private IHouseImageService houseImageService;
    @Autowired
    private IFavoriteService favoriteService;
    @Autowired
    private RegionMapper regionMapper;

    // ========== 会员发布房源 ==========
    @Override
    @Transactional
    public void publish(Integer userId, HousePublishDTO dto) {
        // 1. 校验用户必须是会员（userType = 2）
        User user = userMapper.selectById(userId);
        if (user == null || user.getUserType() != 2) {
            throw new MyException(ErrorType.ERROR, "仅会员可发布房源");
        }

        // 2. 校验小区存在
        Community community = communityMapper.selectById(dto.getCommunityId());
        if (community == null) {
            throw new MyException(ErrorType.INSERT_ERROR, "所选小区不存在");
        }

        // 3. 创建房源记录
        House house = new House();
        BeanUtils.copyProperties(dto, house);
        house.setPublisherId(userId);
        house.setListingTime(LocalDateTime.now());
        house.setSalesStatus(1);          // 上架
        house.setAuditStatus(0);          // 待审核
        houseMapper.insert(house);

        // 4. 保存房屋图片（户型图 + 预览图）
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            List<HouseImage> images = dto.getImageUrls().stream()
                    .map(url -> {
                        HouseImage img = new HouseImage();
                        img.setHouseId(house.getId());
                        img.setUrl(url);
                        // 简单约定：第一张为封面（但封面单独存了coverImage），这里全部设为预览图类型=2
                        img.setType(2);       // 预览图
                        img.setSort(0);
                        return img;
                    }).collect(Collectors.toList());
            houseImageService.saveBatch(images);
        }
    }

    // ========== 经纪人/管理员审核房源 ==========
    @Override
    @Transactional
    public void audit(Integer userId, Integer houseId, Integer auditStatus, String auditRemark) {
        // 1. 获取当前操作者
        User auditor = userMapper.selectById(userId);
        if (auditor == null) {
            throw new MyException(ErrorType.ERROR, "用户不存在");
        }
        Integer userType = auditor.getUserType();
        // 允许审核的角色：经纪人(3)、管理员(4)、超级管理员(5)
        if (userType != 3 && userType != 4 && userType != 5) {
            throw new MyException(ErrorType.ERROR, "无审核权限");
        }

        // 2. 查询房源
        House house = houseMapper.selectById(houseId);
        if (house == null) {
            throw new MyException(ErrorType.UPDATE_ERROR, "房源不存在");
        }
        // 只能审核待审核状态的房源
        if (house.getAuditStatus() != 0) {
            throw new MyException(ErrorType.UPDATE_ERROR, "该房源已被审核，不可重复操作");
        }

        // 3. 经纪人需要校验区域权限
        if (userType == 3) {
            // 3.1 查询经纪人负责的区域（region_id列表）
            List<AgentRegion> agentRegions = agentRegionMapper.selectList(
                    new LambdaQueryWrapper<AgentRegion>()
                            .eq(AgentRegion::getAgentUserId, userId)
            );
            if (agentRegions.isEmpty()) {
                throw new MyException(ErrorType.ERROR, "您未被分配任何区域，无法审核房源");
            }
            Set<Integer> allowedRegionIds = agentRegions.stream()
                    .map(AgentRegion::getRegionId)
                    .collect(Collectors.toSet());

            // 3.2 查询房源所在小区的区域ID
            Community community = communityMapper.selectById(house.getCommunityId());
            if (community == null) {
                throw new MyException(ErrorType.ERROR, "小区信息异常");
            }
            if (!allowedRegionIds.contains(community.getRegionId())) {
                throw new MyException(ErrorType.ERROR, "您只能审核自己负责区域内的房源");
            }
        }

        // 4. 执行审核（更新状态、审核人、时间、备注）
        house.setAuditStatus(auditStatus);   // 1=通过, 2=拒绝
        house.setAuditorId(userId);
        house.setAuditTime(LocalDateTime.now());
        house.setAuditRemark(auditRemark);
        houseMapper.updateById(house);

        // 可选：如果审核不通过，可同时将销售状态改为下架（视业务需求）
        if (auditStatus == 2) {
            house.setSalesStatus(0);
            houseMapper.updateById(house);
        }
    }
    //房屋详情
    @Override
    public HouseDetailVO getDetailById(Integer houseId, Integer userId, Integer userType) {
        // 1. 房屋
        House house = houseMapper.selectById(houseId);
        if (house == null) {
            throw new MyException(ErrorType.ERROR, "房源不存在");
        }

        // 2. 小区
        Community community = communityMapper.selectById(house.getCommunityId());
        if (community == null) {
            throw new MyException(ErrorType.ERROR, "小区信息异常");
        }

        // 3. 区域
        Region region = regionMapper.selectById(community.getRegionId());

        // 4. 发布者
        User publisher = userMapper.selectById(house.getPublisherId());
        String publisherName = publisher != null ? publisher.getName() : "未知";
        String publisherAvatar = publisher != null ? publisher.getAvatar() : null;

        // 5. 图片
        List<HouseImage> images = houseImageService.list(
                new LambdaQueryWrapper<HouseImage>()
                        .eq(HouseImage::getHouseId, houseId)
                        .orderByAsc(HouseImage::getSort)
        );
        List<String> layoutImages = new ArrayList<>();
        List<String> previewImages = new ArrayList<>();
        for (HouseImage img : images) {
            if (img.getType() == 1) {
                layoutImages.add(img.getUrl());
            } else if (img.getType() == 2) {
                previewImages.add(img.getUrl());
            }
        }

        // 6. 收藏状态
        boolean isFavorite = false;
        if (userId != null) {
            Favorite fav = favoriteService.getOne(
                    new LambdaQueryWrapper<Favorite>()
                            .eq(Favorite::getUserId, userId)
                            .eq(Favorite::getHouseId, houseId)
            );
            isFavorite = fav != null;
        }

        // 7. 组装 VO
        HouseDetailVO vo = new HouseDetailVO();
        BeanUtils.copyProperties(house, vo);
        vo.setCommunityName(community.getName());
        vo.setCommunityAddress(community.getAddress());
        vo.setBuildingAge(community.getBuildingAge());
        vo.setPropertyName(community.getPropertyName());
        vo.setLongitude(community.getLongitude());
        vo.setLatitude(community.getLatitude());
        vo.setRegionName(region != null ? region.getRegionName() : "");
        vo.setPublisherName(publisherName);
        vo.setPublisherAvatar(publisherAvatar);
        vo.setLayoutImages(layoutImages);
        vo.setPreviewImages(previewImages);
        vo.setIsFavorite(isFavorite);

        // 【权限控制下沉到 Service】
        boolean isAgentOrAdmin = (userType != null && (userType == 3 || userType == 4 || userType == 5));
        if (isAgentOrAdmin) {
            vo.setBuilding(house.getBuilding());
            vo.setUnit(house.getUnit());
            vo.setRoomNumber(house.getRoomNumber());
            vo.setAuditStatus(house.getAuditStatus());
            vo.setPublisherId(house.getPublisherId());
        }
        // 普通用户不设值，字段自然为 null

        return vo;
    }



    private void applyCommunityFilters(HouseQueryDTO dto, LambdaQueryWrapper<House> wrapper) {
        Set<Integer> communityIds = null;

        // 区域筛选
        if (dto.getRegionId() != null) {
            List<Community> list = communityMapper.selectList(
                    new LambdaQueryWrapper<Community>()
                            .select(Community::getId)
                            .eq(Community::getRegionId, dto.getRegionId())
            );
            communityIds = list.stream().map(Community::getId).collect(Collectors.toSet());
            if (communityIds.isEmpty()) {
                wrapper.apply("1=0"); // 无匹配小区，强制返回空结果
                return;
            }
        }

        // 建筑年代筛选
        if (StringUtils.hasText(dto.getBuildingAgeMin()) || StringUtils.hasText(dto.getBuildingAgeMax())) {
            LambdaQueryWrapper<Community> ageWrapper = new LambdaQueryWrapper<Community>()
                    .select(Community::getId);
            if (StringUtils.hasText(dto.getBuildingAgeMin())) ageWrapper.ge(Community::getBuildingAge, dto.getBuildingAgeMin());
            if (StringUtils.hasText(dto.getBuildingAgeMax())) ageWrapper.le(Community::getBuildingAge, dto.getBuildingAgeMax());
            List<Community> ageList = communityMapper.selectList(ageWrapper);
            Set<Integer> ageIds = ageList.stream().map(Community::getId).collect(Collectors.toSet());
            if (communityIds == null) {
                communityIds = ageIds;
            } else {
                communityIds.retainAll(ageIds);
            }
            if (communityIds.isEmpty()) {
                wrapper.apply("1=0");
                return;
            }
        }

        // 将社区ID限制应用到房源查询
        if (communityIds != null) {
            wrapper.in(House::getCommunityId, communityIds);
        }
    }

    private void applyBasicFilters(HouseQueryDTO dto, LambdaQueryWrapper<House> wrapper) {
        if (dto.getRentMin() != null) wrapper.ge(House::getRent, dto.getRentMin());
        if (dto.getRentMax() != null) wrapper.le(House::getRent, dto.getRentMax());
        if (dto.getAreaMin() != null) wrapper.ge(House::getArea, dto.getAreaMin());
        if (dto.getAreaMax() != null) wrapper.le(House::getArea, dto.getAreaMax());
        if (dto.getFloorMin() != null) wrapper.ge(House::getFloor, dto.getFloorMin());
        if (dto.getFloorMax() != null) wrapper.le(House::getFloor, dto.getFloorMax());
        if (dto.getRentalMethod() != null) wrapper.eq(House::getRentalMethod, dto.getRentalMethod());
        if (dto.getDecoration() != null) wrapper.eq(House::getDecoration, dto.getDecoration());
        if (dto.getElevatorType() != null) wrapper.eq(House::getElevatorType, dto.getElevatorType());

        if (dto.getLayouts() != null && !dto.getLayouts().isEmpty()) {
            wrapper.in(House::getLayout, dto.getLayouts());
        }
        if (dto.getOrientations() != null && !dto.getOrientations().isEmpty()) {
            wrapper.in(House::getOrientation, dto.getOrientations());
        }
        if (dto.getListingTimeStart() != null) wrapper.ge(House::getListingTime, dto.getListingTimeStart());
        if (dto.getListingTimeEnd() != null) wrapper.le(House::getListingTime, dto.getListingTimeEnd());

        // 设施多选（唯一必须用apply的地方）
        if (dto.getFacilities() != null && !dto.getFacilities().isEmpty()) {
            for (Integer fac : dto.getFacilities()) {
                wrapper.apply("FIND_IN_SET({0}, facilities)", fac);
            }
        }
    }

    private void applyKeywordFilter(HouseQueryDTO dto, LambdaQueryWrapper<House> wrapper) {
        if (!StringUtils.hasText(dto.getKeyword())) return;

        String kw = dto.getKeyword();

        // 先查出匹配的小区ID
        Set<Integer> keywordCommunityIds = communityMapper.selectList(
                new LambdaQueryWrapper<Community>()
                        .select(Community::getId)
                        .like(Community::getName, kw)
        ).stream().map(Community::getId).collect(Collectors.toSet());

        // 使用final变量给Lambda
        final Set<Integer> finalIds = keywordCommunityIds.isEmpty() ? null : keywordCommunityIds;

        wrapper.and(w -> {
            w.like(House::getLocation, kw)
                    .or()
                    .like(House::getDescription, kw);
            if (finalIds != null) {
                w.or().in(House::getCommunityId, finalIds);
            }
        });
    }

    private void applyMapFilter(HouseQueryDTO dto, LambdaQueryWrapper<House> wrapper) {
        if (dto.getCenterLng() == null || dto.getCenterLat() == null || dto.getRadius() == null) return;

        String haversine = "6371 * acos(cos(radians(" + dto.getCenterLat() +
                ")) * cos(radians((SELECT latitude FROM t_community c WHERE c.id = community_id)))" +
                " * cos(radians((SELECT longitude FROM t_community c WHERE c.id = community_id)) - radians(" +
                dto.getCenterLng() + ")) + sin(radians(" + dto.getCenterLat() +
                ")) * sin(radians((SELECT latitude FROM t_community c WHERE c.id = community_id))))";
        wrapper.apply(haversine + " <= {0}", dto.getRadius());
    }

    private void applySort(HouseQueryDTO dto, LambdaQueryWrapper<House> wrapper) {
        String field = dto.getSortField();
        boolean asc = Boolean.TRUE.equals(dto.getSortAsc());
        if ("rent".equals(field)) {
            wrapper.orderBy(true, asc, House::getRent);
        } else if ("area".equals(field)) {
            wrapper.orderBy(true, asc, House::getArea);
        } else {
            wrapper.orderByDesc(House::getListingTime);
        }
    }

    private List<HouseListVO> assembleHouseListVO(List<House> houses, Integer userType) {
        if (houses.isEmpty()) return Collections.emptyList();

        // 批量查社区
        Set<Integer> commIds = houses.stream().map(House::getCommunityId).collect(Collectors.toSet());
        Map<Integer, Community> commMap = communityMapper.selectBatchIds(commIds)
                .stream().collect(Collectors.toMap(Community::getId, c -> c));

        // 批量查区域
        Set<Integer> regionIds = commMap.values().stream().map(Community::getRegionId).collect(Collectors.toSet());
        Map<Integer, String> regionNameMap = new HashMap<>();
        if (!regionIds.isEmpty()) {
            regionNameMap = regionMapper.selectBatchIds(regionIds)
                    .stream().collect(Collectors.toMap(Region::getId, Region::getRegionName));
        }

        boolean isAgentOrAdmin = userType != null && (userType == 3 || userType == 4 || userType == 5);
        List<HouseListVO> voList = new ArrayList<>();
        for (House h : houses) {
            HouseListVO vo = new HouseListVO();
            BeanUtils.copyProperties(h, vo, "building", "unit", "roomNumber", "auditStatus", "publisherId");
            Community c = commMap.get(h.getCommunityId());
            if (c != null) {
                vo.setCommunityName(c.getName());
                vo.setRegionName(regionNameMap.getOrDefault(c.getRegionId(), ""));
            }
            if (isAgentOrAdmin) {
                vo.setBuilding(h.getBuilding());
                vo.setUnit(h.getUnit());
                vo.setRoomNumber(h.getRoomNumber());
                vo.setAuditStatus(h.getAuditStatus());
                vo.setPublisherId(h.getPublisherId());
            }
            voList.add(vo);
        }
        return voList;
    }


    public IPage<HouseListVO> searchHouses(HouseQueryDTO dto, Integer userType) {
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();

        // 默认只查上架且审核通过的房源
        wrapper.eq(House::getSalesStatus, dto.getSalesStatus() != null ? dto.getSalesStatus() : 1);
        wrapper.eq(House::getAuditStatus, dto.getAuditStatus() != null ? dto.getAuditStatus() : 1);

        // 依次叠加各类筛选条件
        applyCommunityFilters(dto, wrapper);
        applyBasicFilters(dto, wrapper);
        applyKeywordFilter(dto, wrapper);
        applyMapFilter(dto, wrapper);

        // 排序
        applySort(dto, wrapper);

        // 分页查询
        Page<House> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<House> housePage = houseMapper.selectPage(page, wrapper);

        // 组装VO
        List<HouseListVO> voList = assembleHouseListVO(housePage.getRecords(), userType);

        IPage<HouseListVO> resultPage = new Page<>(housePage.getCurrent(), housePage.getSize(), housePage.getTotal());
        resultPage.setRecords(voList);
        return resultPage;
    }
}
