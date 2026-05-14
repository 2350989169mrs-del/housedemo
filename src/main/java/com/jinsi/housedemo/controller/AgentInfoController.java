package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.AgentInfo;
import com.jinsi.housedemo.service.IAgentInfoService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent-info")
public class AgentInfoController {

    @Autowired
    private IAgentInfoService agentInfoService;

    @GetMapping("/list")
    public ResultData list() {
        List<AgentInfo> list = agentInfoService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<AgentInfo> page = new Page<>(current, size);
        Page<AgentInfo> result = agentInfoService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        AgentInfo agentInfo = agentInfoService.getById(id);
        if (agentInfo != null) {
            return new ResultData("data", agentInfo, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }
    // controller/AgentInfoController.java 追加或替换
    @PostMapping("/createAgent")
    public ResultData createAgent(@RequestAttribute("userId") Integer currentUserId,
                                  @RequestParam String account,
                                  @RequestParam String password,
                                  @RequestParam String realName,
                                  @RequestParam String idCard,
                                  @RequestParam(required = false) Integer storeId,
                                  @RequestParam(required = false) String phone) {
        agentInfoService.createAgent(currentUserId, account, password, realName, idCard, storeId, phone);
        return new ResultData("经纪人创建成功");
    }

    @PutMapping("/{agentUserId}/status")
    public ResultData updateAgentStatus(@RequestAttribute("userId") Integer currentUserId,
                                        @PathVariable Integer agentUserId,
                                        @RequestParam Integer status) {
        agentInfoService.updateAgentStatus(currentUserId, agentUserId, status);
        return new ResultData("操作成功");
    }

    @PostMapping
    public ResultData save(@RequestBody AgentInfo agentInfo) {
        boolean success = agentInfoService.save(agentInfo);
        if (success) return new ResultData("添加成功");
        return new ResultData(500, "添加失败");
    }

    @PutMapping
    public ResultData update(@RequestBody AgentInfo agentInfo) {
        boolean success = agentInfoService.updateById(agentInfo);
        if (success) return new ResultData("修改成功");
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        agentInfoService.removeById(id);
        return new ResultData("删除成功");
    }
}