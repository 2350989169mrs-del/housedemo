package com.jinsi.housedemo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShowingRecordVO {
    private Integer id;
    private Integer appointmentId;
    private Integer agentUserId;
    private LocalDateTime showingTime;
    private Integer clientIntention;       // 1高 2中 3低 4暂无意向
    private Integer clientSatisfaction;    // 1非常满意 2满意 3一般 4不满意
    private String remark;
    private LocalDateTime createTime;

    // 关联信息（方便展示）
    private String houseLocation;
    private String clientName;
    private String agentName;
}
