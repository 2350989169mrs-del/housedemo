package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.AdminInfo;
import com.jinsi.housedemo.service.IAdminInfoService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-info")
public class AdminInfoController {

    @Autowired
    private IAdminInfoService adminInfoService;

    @GetMapping("/list")
    public ResultData list() {
        List<AdminInfo> list = adminInfoService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<AdminInfo> page = new Page<>(current, size);
        Page<AdminInfo> result = adminInfoService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        AdminInfo adminInfo = adminInfoService.getById(id);
        if (adminInfo != null) {
            return new ResultData("data", adminInfo, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }
    @PostMapping("/createAdmin")
    public ResultData createAdmin(@RequestAttribute("userId") Integer currentUserId,
                                  @RequestParam String account,
                                  @RequestParam String password,
                                  @RequestParam String realName) {
        adminInfoService.createAdmin(currentUserId, account, password, realName);
        return new ResultData("管理员创建成功");
    }

    @PutMapping("/{adminUserId}/status")
    public ResultData updateAdminStatus(@RequestAttribute("userId") Integer currentUserId,
                                        @PathVariable Integer adminUserId,
                                        @RequestParam Integer status) {

        adminInfoService.updateAdminStatus(currentUserId, adminUserId, status);
        return new ResultData("操作成功");
    }
}
