package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.Store;
import com.jinsi.housedemo.service.IStoreService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private IStoreService storeService;

    @GetMapping("/list")
    public ResultData list() {
        List<Store> list = storeService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<Store> page = new Page<>(current, size);
        Page<Store> result = storeService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        Store store = storeService.getById(id);
        if (store != null) {
            return new ResultData("data", store, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PostMapping
    public ResultData save(@RequestBody Store store) {
        boolean success = storeService.save(store);
        if (success) {
            return new ResultData("添加成功");
        }
        return new ResultData(500, "添加失败");
    }

    @PutMapping
    public ResultData update(@RequestBody Store store) {
        boolean success = storeService.updateById(store);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = storeService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }
}
