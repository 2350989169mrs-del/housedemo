package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.DictType;
import com.jinsi.housedemo.service.IDictTypeService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict-type")
public class DictTypeController {

    @Autowired
    private IDictTypeService dictTypeService;

    @GetMapping("/list")
    public ResultData list() {
        List<DictType> list = dictTypeService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<DictType> page = new Page<>(current, size);
        Page<DictType> result = dictTypeService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        DictType dictType = dictTypeService.getById(id);
        if (dictType != null) {
            return new ResultData("data", dictType, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PostMapping
    public ResultData save(@RequestBody DictType dictType) {
        boolean success = dictTypeService.save(dictType);
        if (success) {
            return new ResultData("添加成功");
        }
        return new ResultData(500, "添加失败");
    }

    @PutMapping
    public ResultData update(@RequestBody DictType dictType) {
        boolean success = dictTypeService.updateById(dictType);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = dictTypeService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }
}
