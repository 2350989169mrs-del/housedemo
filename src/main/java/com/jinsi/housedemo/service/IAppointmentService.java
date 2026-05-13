package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.Appointment;
import com.jinsi.housedemo.entity.AppointmentVO;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService extends IService<Appointment> {

    /** 会员创建预约 */
    Appointment createAppointment(Integer userId, Integer houseId, LocalDateTime appointmentTime);
    /** 会员查看自己的预约列表 */
    List<AppointmentVO> listByClient(Integer userId);
    /** 经纪人查看待处理预约（只负责自己区域） */
    List<AppointmentVO> listForAgent(Integer agentUserId);
    /** 经纪人确认预约 */
    void confirm(Integer appointmentId, Integer agentUserId);
    /** 经纪人拒绝预约 */
    void reject(Integer appointmentId, Integer agentUserId);
    /** 会员取消预约 */
    void cancelByClient(Integer appointmentId, Integer userId);
}
