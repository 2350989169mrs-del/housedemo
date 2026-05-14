package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.User;
import com.jinsi.housedemo.entity.UserCenterVO;
import com.jinsi.housedemo.service.IUserService;
import com.jinsi.housedemo.util.ErrorType;
import com.jinsi.housedemo.util.JwtUtil;
import com.jinsi.housedemo.util.MyException;
import com.jinsi.housedemo.util.ResultData;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public ResultData list() {
        List<User> list = userService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<User> page = new Page<>(current, size);
        Page<User> result = userService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) {
            return new ResultData("data", user, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PostMapping("/login")
    public ResultData login(@RequestParam @NotBlank(message = "手机号不能为空") String account,
                            @RequestParam @NotBlank(message = "密码不能为空") String password) {
        // 查用户
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getAccount, account));
        if (user == null) {
            throw new MyException(ErrorType.ERROR, "账号或密码错误");
        }
        // 验密码
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new MyException(ErrorType.ERROR, "账号或密码错误");
        }
        // 验状态
        if (user.getAccountStatus() != 1) {
            throw new MyException(ErrorType.ERROR, "账户异常，请联系客服");
        }
        // 生成 Token
        String token = JwtUtil.generateToken(user.getId(), user.getUserType());
        // 返回
        return new ResultData(
                new String[]{"token", "userId", "userType", "name"},
                new Object[]{token, user.getId(), user.getUserType(), user.getName()},
                "登录成功"
        );
    }

    // ==================== 注册 ====================

    @PostMapping("/register")
    public ResultData register(@RequestParam @NotBlank(message = "手机号不能为空") String account,
                               @RequestParam @NotBlank(message = "密码不能为空")
                               @Size(min = 6, message = "密码至少6位") String password,
                               @RequestParam(required = false) String name) {
        userService.register(account, password, name);
        return new ResultData("注册成功，请登录");
    }

    // ==================== 登出 ====================

    @PostMapping("/logout")
    public ResultData logout() {
        return new ResultData("已退出登录");
    }

    // ==================== 修改密码 ====================

    @PutMapping("/change-password")
    public ResultData changePassword(@RequestAttribute Integer userId,
                                     @RequestParam @NotBlank(message = "旧密码不能为空") String oldPassword,
                                     @RequestParam @NotBlank(message = "新密码不能为空")
                                     @Size(min = 6, message = "新密码至少6位") String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return new ResultData("密码修改成功，请重新登录");
    }

    // ==================== 更新资料 ====================

    @PutMapping("/profile")
    public ResultData updateProfile(@RequestAttribute Integer userId,
                                    @RequestParam(required = false) String avatar,
                                    @RequestParam(required = false) String name) {
        userService.updateProfile(userId, avatar, name);
        return new ResultData("资料更新成功");
    }

    @GetMapping("/center")
    public ResultData center(@RequestAttribute Integer userId) {
        UserCenterVO vo = userService.getUserCenter(userId);
        return new ResultData("data", vo, "查询成功");
    }
}