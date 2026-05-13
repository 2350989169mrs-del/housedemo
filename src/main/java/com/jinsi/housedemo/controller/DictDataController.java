package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.DictData;
import com.jinsi.housedemo.entity.DictDataVO;
import com.jinsi.housedemo.service.IDictDataService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dict-data")
public class DictDataController {

    @Autowired
    private IDictDataService dictDataService;

    @GetMapping("/list")
    public ResultData list() {
        List<DictData> list = dictDataService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<DictData> page = new Page<>(current, size);
        Page<DictData> result = dictDataService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        DictData dictData = dictDataService.getById(id);
        if (dictData != null) {
            return new ResultData("data", dictData, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PostMapping
    public ResultData save(@RequestBody DictData dictData) {
        boolean success = dictDataService.save(dictData);
        if (success) {
            return new ResultData("添加成功");
        }
        return new ResultData(500, "添加失败");
    }

    @PutMapping
    public ResultData update(@RequestBody DictData dictData) {
        boolean success = dictDataService.updateById(dictData);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = dictDataService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }

    // controller/DictDataController.java 追加方法
    @GetMapping("/batch")
    public ResultData batch(@RequestParam String types) {
        List<String> typeList = Arrays.asList(types.split(","));
        Map<String, List<DictDataVO>> dictMap = dictDataService.getBatchDictByTypes(typeList);
        return new ResultData("dicts", dictMap, "字典数据获取成功");
    }
}
