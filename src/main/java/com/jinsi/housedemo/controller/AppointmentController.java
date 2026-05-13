package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.Appointment;
import com.jinsi.housedemo.entity.AppointmentVO;
import com.jinsi.housedemo.service.IAppointmentService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private IAppointmentService appointmentService;

    @GetMapping("/list")
    public ResultData list() {
        List<Appointment> list = appointmentService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<Appointment> page = new Page<>(current, size);
        Page<Appointment> result = appointmentService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        Appointment appointment = appointmentService.getById(id);
        if (appointment != null) {
            return new ResultData("data", appointment, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PutMapping
    public ResultData update(@RequestBody Appointment appointment) {
        boolean success = appointmentService.updateById(appointment);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = appointmentService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }

    // 会员创建预约
    @PostMapping("/create")
    public ResultData create(@RequestAttribute Integer userId,
                             @RequestParam Integer houseId,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime appointmentTime) {
        Appointment app = appointmentService.createAppointment(userId, houseId, appointmentTime);
        return new ResultData("data", app, "预约成功，等待经纪人确认");
    }

    // 会员自己的预约列表
    @GetMapping("/my-list")
    public ResultData myList(@RequestAttribute Integer userId) {
        List<AppointmentVO> list = appointmentService.listByClient(userId);
        return new ResultData("data", list, "查询成功");
    }

    // 经纪人待处理预约列表
    @GetMapping("/agent-list")
    public ResultData agentList(@RequestAttribute Integer userId) {
        List<AppointmentVO> list = appointmentService.listForAgent(userId);
        return new ResultData("data", list, "查询成功");
    }

    // 经纪人确认预约
    @PutMapping("/{id}/confirm")
    public ResultData confirm(@PathVariable Integer id,
                              @RequestAttribute Integer userId) {
        appointmentService.confirm(id, userId);
        return new ResultData("已确认");
    }

    // 经纪人拒绝预约
    @PutMapping("/{id}/reject")
    public ResultData reject(@PathVariable Integer id,
                             @RequestAttribute Integer userId) {
        appointmentService.reject(id, userId);
        return new ResultData("已拒绝");
    }

    // 会员取消预约
    @PutMapping("/{id}/cancel")
    public ResultData cancel(@PathVariable Integer id,
                             @RequestAttribute Integer userId) {
        appointmentService.cancelByClient(id, userId);
        return new ResultData("已取消");
    }
}
