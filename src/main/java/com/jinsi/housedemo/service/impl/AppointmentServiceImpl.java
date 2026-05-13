package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.*;
import com.jinsi.housedemo.mapper.*;
import com.jinsi.housedemo.service.IAppointmentService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements IAppointmentService {
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private AgentInfoMapper agentInfoMapper;
    @Autowired
    private AgentRegionMapper agentRegionMapper;

    @Override
    @Transactional
    public Appointment createAppointment(Integer userId, Integer houseId, LocalDateTime appointmentTime) {
        // 1. 必须是会员
        User user = userMapper.selectById(userId);
        if (user == null || user.getUserType() != 2) {
            throw new MyException(ErrorType.ERROR, "仅会员可预约看房");
        }
        // 2. 房源必须存在且上架 + 审核通过
        House house = houseMapper.selectById(houseId);
        if (house == null || house.getSalesStatus() != 1 || house.getAuditStatus() != 1) {
            throw new MyException(ErrorType.INSERT_ERROR, "房源不可预约");
        }
        // 3. 不能重复预约同一房源（状态为待确认或已确认的不能再次预约）
        long count = this.count(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getClientUserId, userId)
                .eq(Appointment::getHouseId, houseId)
                .in(Appointment::getStatus, 1, 2)); // 待确认(1)、已确认(2)
        if (count > 0) {
            throw new MyException(ErrorType.INSERT_ERROR, "您已有该房源的有效预约");
        }
        // 4. 创建
        Appointment app = new Appointment();
        app.setHouseId(houseId);
        app.setClientUserId(userId);
        app.setAppointmentTime(appointmentTime);
        app.setStatus(1);   // 待确认
        app.setCreateTime(LocalDateTime.now());
        app.setUpdateTime(LocalDateTime.now());
        this.save(app);
        return app;
    }

    @Override
    public List<AppointmentVO> listByClient(Integer userId) {
        List<Appointment> list = this.list(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getClientUserId, userId)
                .orderByDesc(Appointment::getCreateTime));
        return assembleAppointmentVO(list);
    }

    @Override
    public List<AppointmentVO> listForAgent(Integer agentUserId) {
        // 1. 获取经纪人负责的区域 ID
        List<AgentRegion> regions = agentRegionMapper.selectList(
                new LambdaQueryWrapper<AgentRegion>()
                        .eq(AgentRegion::getAgentUserId, agentUserId));
        if (regions.isEmpty()) {
            return new ArrayList<>(); // 无负责区域，返回空
        }
        Set<Integer> regionIds = regions.stream().map(AgentRegion::getRegionId).collect(Collectors.toSet());

        // 2. 找出这些区域下的所有小区 ID
        List<Community> communities = communityMapper.selectList(
                new LambdaQueryWrapper<Community>()
                        .in(Community::getRegionId, regionIds));
        if (communities.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Integer> communityIds = communities.stream().map(Community::getId).collect(Collectors.toSet());

        // 3. 找出这些小区下的所有房源 ID
        List<House> houses = houseMapper.selectList(
                new LambdaQueryWrapper<House>()
                        .in(House::getCommunityId, communityIds));
        if (houses.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Integer> houseIds = houses.stream().map(House::getId).collect(Collectors.toSet());

        // 4. 查询这些房源下状态为待确认(1)的预约
        List<Appointment> list = this.list(new LambdaQueryWrapper<Appointment>()
                .in(Appointment::getHouseId, houseIds)
                .eq(Appointment::getStatus, 1)
                .orderByAsc(Appointment::getCreateTime));

        return assembleAppointmentVO(list);
    }

    @Override
    @Transactional
    public void confirm(Integer appointmentId, Integer agentUserId) {
        Appointment app = this.getById(appointmentId);
        if (app == null || app.getStatus() != 1) {
            throw new MyException(ErrorType.UPDATE_ERROR, "预约不存在或状态不可确认");
        }
        // 校验区域权限（确保经纪人有权处理该房源）
        House house = houseMapper.selectById(app.getHouseId());
        if (house == null) throw new MyException(ErrorType.ERROR, "房源不存在");
        Community community = communityMapper.selectById(house.getCommunityId());
        if (community == null) throw new MyException(ErrorType.ERROR, "小区信息异常");

        // 经纪人是否负责该区域
        boolean hasAuth = agentRegionMapper.exists(new LambdaQueryWrapper<AgentRegion>()
                .eq(AgentRegion::getAgentUserId, agentUserId)
                .eq(AgentRegion::getRegionId, community.getRegionId()));
        if (!hasAuth) {
            throw new MyException(ErrorType.ERROR, "您不负责该区域，无法处理预约");
        }
        app.setAgentUserId(agentUserId);
        app.setStatus(2);
        app.setUpdateTime(LocalDateTime.now());
        this.updateById(app);
    }

    @Override
    @Transactional
    public void reject(Integer appointmentId, Integer agentUserId) {
        Appointment app = this.getById(appointmentId);
        if (app == null || app.getStatus() != 1) {
            throw new MyException(ErrorType.UPDATE_ERROR, "预约不存在或状态不可拒绝");
        }
        // 同样校验区域权限（与 confirm 相同，可抽取方法，这里先简单重复）
        House house = houseMapper.selectById(app.getHouseId());
        if (house == null) throw new MyException(ErrorType.ERROR, "房源不存在");
        Community community = communityMapper.selectById(house.getCommunityId());
        if (community == null) throw new MyException(ErrorType.ERROR, "小区信息异常");
        boolean hasAuth = agentRegionMapper.exists(new LambdaQueryWrapper<AgentRegion>()
                .eq(AgentRegion::getAgentUserId, agentUserId)
                .eq(AgentRegion::getRegionId, community.getRegionId()));
        if (!hasAuth) {
            throw new MyException(ErrorType.ERROR, "您不负责该区域");
        }
        app.setStatus(5); // 已拒绝
        app.setUpdateTime(LocalDateTime.now());
        this.updateById(app);
    }

    @Override
    @Transactional
    public void cancelByClient(Integer appointmentId, Integer userId) {
        Appointment app = this.getById(appointmentId);
        if (app == null || !app.getClientUserId().equals(userId)) {
            throw new MyException(ErrorType.UPDATE_ERROR, "无权操作该预约");
        }
        if (app.getStatus() != 1 && app.getStatus() != 2) {
            throw new MyException(ErrorType.UPDATE_ERROR, "当前状态不可取消");
        }
        app.setStatus(4); // 已取消
        app.setUpdateTime(LocalDateTime.now());
        this.updateById(app);
    }

    // ========= 辅助方法：将 Appointment 列表组装为 VO =========
    private List<AppointmentVO> assembleAppointmentVO(List<Appointment> appointments) {
        if (appointments.isEmpty()) return new ArrayList<>();

        // 批量查房源
        Set<Integer> houseIds = appointments.stream().map(Appointment::getHouseId).collect(Collectors.toSet());
        List<House> houses = houseMapper.selectBatchIds(houseIds);
        Map<Integer, House> houseMap = houses.stream().collect(Collectors.toMap(House::getId, h -> h));

        // 批量查小区
        Set<Integer> commIds = houses.stream().map(House::getCommunityId).collect(Collectors.toSet());
        List<Community> communities = communityMapper.selectBatchIds(commIds);
        Map<Integer, Community> commMap = communities.stream().collect(Collectors.toMap(Community::getId, c -> c));

        // 批量查预约人
        Set<Integer> clientIds = appointments.stream().map(Appointment::getClientUserId).collect(Collectors.toSet());
        List<User> clients = userMapper.selectBatchIds(clientIds);
        Map<Integer, User> clientUserMap = clients.stream().collect(Collectors.toMap(User::getId, u -> u));

        // 批量查经纪人姓名（agentUserId 可能为空）
        Set<Integer> agentIds = appointments.stream().map(Appointment::getAgentUserId)
                .filter(id -> id != null).collect(Collectors.toSet());
        Map<Integer, String> agentNameMap = new HashMap<>();
        if (!agentIds.isEmpty()) {
            List<User> agents = userMapper.selectBatchIds(agentIds);
            agentNameMap = agents.stream().collect(Collectors.toMap(User::getId, User::getName));
        }

        List<AppointmentVO> voList = new ArrayList<>();
        for (Appointment app : appointments) {
            AppointmentVO vo = new AppointmentVO();
            BeanUtils.copyProperties(app, vo);

            House house = houseMap.get(app.getHouseId());
            if (house != null) {
                vo.setHouseCover(house.getCoverImage());
                vo.setHouseLocation(house.getLocation());
                vo.setRent(house.getRent());
                Community comm = commMap.get(house.getCommunityId());
                if (comm != null) {
                    vo.setCommunityName(comm.getName());
                }
            }
            User client = clientUserMap.get(app.getClientUserId());
            if (client != null) {
                vo.setClientName(client.getName());
            }
            if (app.getAgentUserId() != null) {
                vo.setAgentName(agentNameMap.getOrDefault(app.getAgentUserId(), ""));
            }
            voList.add(vo);
        }
        return voList;
    }
}
