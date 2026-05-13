package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.HouseImage;
import com.jinsi.housedemo.service.IHouseImageService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/house-image")
public class HouseImageController {

    @Autowired
    private IHouseImageService houseImageService;

    @GetMapping("/list")
    public ResultData list() {
        List<HouseImage> list = houseImageService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<HouseImage> page = new Page<>(current, size);
        Page<HouseImage> result = houseImageService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        HouseImage houseImage = houseImageService.getById(id);
        if (houseImage != null) {
            return new ResultData("data", houseImage, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PostMapping
    public ResultData save(@RequestBody HouseImage houseImage) {
        boolean success = houseImageService.save(houseImage);
        if (success) {
            return new ResultData("添加成功");
        }
        return new ResultData(500, "添加失败");
    }

    @PutMapping
    public ResultData update(@RequestBody HouseImage houseImage) {
        boolean success = houseImageService.updateById(houseImage);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = houseImageService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }
}
