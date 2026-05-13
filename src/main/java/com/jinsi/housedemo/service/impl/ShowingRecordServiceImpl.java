package com.jinsi.housedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinsi.housedemo.entity.*;
import com.jinsi.housedemo.mapper.*;
import com.jinsi.housedemo.service.IShowingRecordService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.MyException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ShowingRecordServiceImpl extends ServiceImpl<ShowingRecordMapper, ShowingRecord> implements IShowingRecordService {
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public ShowingRecord createRecord(Integer agentUserId, Integer appointmentId,
                                      LocalDateTime showingTime, Integer clientIntention,
                                      Integer clientSatisfaction, String remark) {
        // 1. 校验预约是否存在且状态为已确认(2)
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getStatus() != 2) {
            throw new MyException(ErrorType.UPDATE_ERROR, "预约状态无效，无法填写带看记录");
        }
        // 2. 校验操作人是该预约的经纪人
        if (!appointment.getAgentUserId().equals(agentUserId)) {
            throw new MyException(ErrorType.ERROR, "您不是该预约的受理经纪人");
        }
        // 3. 防止重复创建（一对一，一个预约只能有一条记录）
        long count = this.count(new LambdaQueryWrapper<ShowingRecord>()
                .eq(ShowingRecord::getAppointmentId, appointmentId));
        if (count > 0) {
            throw new MyException(ErrorType.INSERT_ERROR, "该预约已有带看记录");
        }
        // 4. 创建带看记录
        ShowingRecord record = new ShowingRecord();
        record.setAppointmentId(appointmentId);
        record.setAgentUserId(agentUserId);
        record.setShowingTime(showingTime);
        record.setClientIntention(clientIntention);
        record.setClientSatisfaction(clientSatisfaction);
        record.setRemark(remark);
        record.setCreateTime(LocalDateTime.now());
        this.save(record);

        // 5. 更新预约状态为已完成(3)
        appointment.setStatus(3);
        appointment.setUpdateTime(LocalDateTime.now());
        appointmentMapper.updateById(appointment);

        return record;
    }

    @Override
    public ShowingRecordVO getByAppointmentId(Integer appointmentId) {
        ShowingRecord record = this.getOne(new LambdaQueryWrapper<ShowingRecord>()
                .eq(ShowingRecord::getAppointmentId, appointmentId));
        if (record == null) {
            return null;
        }
        // 组装 VO
        ShowingRecordVO vo = new ShowingRecordVO();
        BeanUtils.copyProperties(record, vo);

        // 预约信息
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment != null) {
            // 房屋信息
            House house = houseMapper.selectById(appointment.getHouseId());
            if (house != null) {
                vo.setHouseLocation(house.getLocation());
            }
            // 客户姓名
            User client = userMapper.selectById(appointment.getClientUserId());
            if (client != null) {
                vo.setClientName(client.getName());
            }
        }
        // 经纪人姓名
        User agent = userMapper.selectById(record.getAgentUserId());
        if (agent != null) {
            vo.setAgentName(agent.getName());
        }
        return vo;
    }
}
