package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.AgentRegion;
import com.jinsi.housedemo.service.IAgentRegionService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent-region")
public class AgentRegionController {

    @Autowired
    private IAgentRegionService agentRegionService;

    @GetMapping("/list")
    public ResultData list() {
        List<AgentRegion> list = agentRegionService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<AgentRegion> page = new Page<>(current, size);
        Page<AgentRegion> result = agentRegionService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        AgentRegion agentRegion = agentRegionService.getById(id);
        if (agentRegion != null) {
            return new ResultData("data", agentRegion, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    @PostMapping
    public ResultData save(@RequestBody AgentRegion agentRegion) {
        boolean success = agentRegionService.save(agentRegion);
        if (success) {
            return new ResultData("添加成功");
        }
        return new ResultData(500, "添加失败");
    }

    @PutMapping
    public ResultData update(@RequestBody AgentRegion agentRegion) {
        boolean success = agentRegionService.updateById(agentRegion);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = agentRegionService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }
}
