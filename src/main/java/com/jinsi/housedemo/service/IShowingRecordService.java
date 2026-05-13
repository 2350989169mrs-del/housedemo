package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.ShowingRecord;
import com.jinsi.housedemo.entity.ShowingRecordVO;

import java.time.LocalDateTime;

public interface IShowingRecordService extends IService<ShowingRecord> {
    /** 经纪人创建带看记录，同步更新预约状态为已完成 */
    ShowingRecord createRecord(Integer agentUserId, Integer appointmentId,
                               LocalDateTime showingTime, Integer clientIntention,
                               Integer clientSatisfaction, String remark);

    /** 根据预约ID获取带看记录 */
    ShowingRecordVO getByAppointmentId(Integer appointmentId);
}
