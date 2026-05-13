package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.Role;
import com.jinsi.housedemo.service.IRoleService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @GetMapping("/list")
    public ResultData list() {
        List<Role> list = roleService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<Role> page = new Page<>(current, size);
        Page<Role> result = roleService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        Role role = roleService.getById(id);
        if (role != null) {
            return new ResultData("data", role, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PostMapping
    public ResultData save(@RequestBody Role role) {
        boolean success = roleService.save(role);
        if (success) {
            return new ResultData("添加成功");
        }
        return new ResultData(500, "添加失败");
    }

    @PutMapping
    public ResultData update(@RequestBody Role role) {
        boolean success = roleService.updateById(role);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = roleService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }
}
