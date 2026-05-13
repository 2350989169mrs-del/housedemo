package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.UserInfo;
import com.jinsi.housedemo.service.IUserInfoService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/user-info")
public class UserInfoController {

    @Autowired
    private IUserInfoService userInfoService;

    @GetMapping("/list")
    public ResultData list() {
        List<UserInfo> list = userInfoService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<UserInfo> page = new Page<>(current, size);
        Page<UserInfo> result = userInfoService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        UserInfo userInfo = userInfoService.getById(id);
        if (userInfo != null) {
            return new ResultData("data", userInfo, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    /**
     * 保存或更新用户扩展信息，并自动升级为会员
     */
    @PostMapping
    public ResultData saveUserInfo(@RequestAttribute("userId") Integer currentUserId,
                                   @RequestParam String realName,
                                   @RequestParam String idCard,
                                   @RequestParam(required = false) Integer gender,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
                                   @RequestParam(required = false) String email) {
        userInfoService.saveAndUpdateUserType(currentUserId, realName, idCard, gender, birthday, email);
        return new ResultData("会员资料保存成功");
    }

    @PutMapping
    public ResultData update(@RequestBody UserInfo userInfo) {
        boolean success = userInfoService.updateById(userInfo);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = userInfoService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }
}
