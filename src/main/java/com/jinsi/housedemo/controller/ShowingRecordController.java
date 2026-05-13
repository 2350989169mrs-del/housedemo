package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.ShowingRecord;
import com.jinsi.housedemo.entity.ShowingRecordVO;
import com.jinsi.housedemo.service.IShowingRecordService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/showing-record")
public class ShowingRecordController {

    @Autowired
    private IShowingRecordService showingRecordService;

    @GetMapping("/list")
    public ResultData list() {
        List<ShowingRecord> list = showingRecordService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                           @RequestParam(defaultValue = "10") Integer size) {
        Page<ShowingRecord> page = new Page<>(current, size);
        Page<ShowingRecord> result = showingRecordService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        ShowingRecord showingRecord = showingRecordService.getById(id);
        if (showingRecord != null) {
            return new ResultData("data", showingRecord, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }


    @PutMapping
    public ResultData update(@RequestBody ShowingRecord showingRecord) {
        boolean success = showingRecordService.updateById(showingRecord);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = showingRecordService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }

    // 经纪人创建带看记录
    @PostMapping("/create")
    public ResultData create(@RequestAttribute Integer userId,
                             @RequestParam Integer appointmentId,
                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime showingTime,
                             @RequestParam Integer clientIntention,
                             @RequestParam Integer clientSatisfaction,
                             @RequestParam(required = false) String remark) {
        ShowingRecord record = showingRecordService.createRecord(userId, appointmentId,
                showingTime, clientIntention, clientSatisfaction, remark);
        return new ResultData("data", record, "带看记录已保存");
    }

    // 根据预约ID查看带看记录
    @GetMapping("/by-appointment/{appointmentId}")
    public ResultData getByAppointmentId(@PathVariable Integer appointmentId) {
        ShowingRecordVO vo = showingRecordService.getByAppointmentId(appointmentId);
        if (vo != null) {
            return new ResultData("data", vo, "查询成功");
        }
        return new ResultData(404, "未找到带看记录");
    }
}