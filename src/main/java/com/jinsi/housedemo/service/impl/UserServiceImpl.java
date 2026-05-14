package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.*;
import com.jinsi.housedemo.mapper.*;
import com.jinsi.housedemo.service.IUserService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现 —— 注册、登录校验、个人中心、改密、资料编辑
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private AgentInfoMapper agentInfoMapper;
    @Autowired
    private AdminInfoMapper adminInfoMapper;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private ShowingRecordMapper showingRecordMapper;

    // ==================== 注册 ====================

    @Override
    @Transactional
    public void register(String account, String password, String name) {
        // 1. 手机号唯一性检查
        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getAccount, account)) > 0) {
            throw new MyException(ErrorType.INSERT_ERROR, "手机号已被注册");
        }

        // 2. 构建用户对象
        User user = new User();
        user.setAccount(account);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt())); // BCrypt 加密
        user.setName(StringUtils.hasText(name) ? name : null);
        user.setUserType(1);             // 普通用户（完善资料后升级为会员 userType=2）
        user.setAccountStatus(1);        // 正常
        user.setRoleId(null);            // 允许为空
        // create_time 由 MyBatis-Plus 自动填充
        userMapper.insert(user);
    }

    // ==================== 个人中心 ====================

    @Override
    public UserCenterVO getUserCenter(Integer userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new MyException(ErrorType.ERROR, "用户不存在");
        }

        UserCenterVO vo = new UserCenterVO();
        vo.setUserId(user.getId());
        vo.setAvatar(user.getAvatar());
        vo.setName(user.getName());
        vo.setAccount(user.getAccount());
        vo.setUserType(user.getUserType());
        vo.setAccountStatus(user.getAccountStatus());
        vo.setCreateTime(user.getCreateTime());

        // 根据角色填充扩展信息
        switch (user.getUserType()) {
            case 1: // 普通用户
                fillCommonUserInfo(userId, vo);
                break;
            case 2: // 会员
                fillCommonUserInfo(userId, vo);
                vo.setPublishCount(
                        houseMapper.selectCount(new LambdaQueryWrapper<House>()
                                .eq(House::getPublisherId, userId))
                );
                break;
            case 3: // 经纪人
                fillAgentInfo(userId, vo);
                // 统计已处理预约数等
                break;
            case 4: // 管理员
                fillAdminInfo(userId, vo);
                break;
            case 5: // 超级管理员
                fillAdminInfo(userId, vo);
                break;
        }

        // 通用统计：收藏数、预约数
        vo.setFavoriteCount(
                favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId))
        );
        vo.setAppointmentCount(
                appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                        .eq(Appointment::getClientUserId, userId)
                        .or()
                        .eq(Appointment::getAgentUserId, userId))
        );

        return vo;
    }

    // ==================== 修改密码 ====================

    /**
     * 修改密码：验证旧密码正确后，BCrypt 加密新密码并更新
     */
    @Override
    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new MyException(ErrorType.ERROR, "用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new MyException(ErrorType.ERROR, "旧密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userMapper.updateById(user);
    }

    // ==================== 更新个人资料 ====================

    /**
     * 更新头像和昵称，允许只传其中一个
     */
    @Override
    @Transactional
    public void updateProfile(Integer userId, String avatar, String name) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new MyException(ErrorType.ERROR, "用户不存在");
        }
        if (StringUtils.hasText(avatar)) {
            user.setAvatar(avatar);
        }
        if (StringUtils.hasText(name)) {
            user.setName(name);
        }
        userMapper.updateById(user);
    }

    // ==================== 私有辅助方法 ====================

    private void fillCommonUserInfo(Integer userId, UserCenterVO vo) {
        UserInfo info = userInfoMapper.selectOne(
                new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getUserId, userId));
        if (info != null) {
            vo.setRealName(info.getRealName());
            vo.setIdCard(info.getIdCard());
            vo.setGender(info.getGender());
            vo.setBirthday(info.getBirthday());
            vo.setEmail(info.getEmail());
        }
    }

    private void fillAgentInfo(Integer userId, UserCenterVO vo) {
        AgentInfo agent = agentInfoMapper.selectOne(
                new LambdaQueryWrapper<AgentInfo>().eq(AgentInfo::getUserId, userId));
        if (agent != null) {
            vo.setRealName(agent.getRealName());
            vo.setIdCard(agent.getIdCard());
            vo.setPhone(agent.getPhone());
            vo.setStoreId(agent.getStoreId());
            if (agent.getStoreId() != null) {
                Store store = storeMapper.selectById(agent.getStoreId());
                if (store != null) {
                    vo.setStoreName(store.getStoreName());
                }
            }
            vo.setLicenseNumber(agent.getLicenseNumber());
            vo.setServiceYears(agent.getServiceYears());
            vo.setIntroduction(agent.getIntroduction());
        }
    }

    private void fillAdminInfo(Integer userId, UserCenterVO vo) {
        AdminInfo admin = adminInfoMapper.selectOne(
                new LambdaQueryWrapper<AdminInfo>().eq(AdminInfo::getUserId, userId));
        if (admin != null) {
            vo.setRealName(admin.getRealName());
            vo.setIdCard(admin.getIdCard());
            vo.setDepartment(admin.getDepartment());
        }
    }
}