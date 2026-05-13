package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.*;
import com.jinsi.housedemo.service.IHouseService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/house")//列出你在这个项目中所有的技术决策
public class HouseController {

    @Autowired
    private IHouseService houseService;

    @GetMapping("/list")
    public ResultData list() {
        List<House> list = houseService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<House> page = new Page<>(current, size);
        Page<House> result = houseService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        House house = houseService.getById(id);
        if (house != null) {
            return new ResultData("data", house, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }



    @PutMapping
    public ResultData update(@RequestBody House house) {
        boolean success = houseService.updateById(house);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = houseService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }

    // 会员发布房源
    @PostMapping("/publish")
    public ResultData publish(@RequestAttribute Integer userId,
                              @RequestBody HousePublishDTO dto) {
        houseService.publish(userId, dto);
        return new ResultData("房源发布成功，等待审核");
    }

    // 经纪人/管理员审核房源
    @PostMapping("/audit")
    public ResultData audit(@RequestAttribute Integer userId,
                            @RequestParam Integer houseId,
                            @RequestParam Integer auditStatus,   // 1通过 2拒绝
                            @RequestParam(required = false) String auditRemark) {
        houseService.audit(userId, houseId, auditStatus, auditRemark);
        String msg = auditStatus == 1 ? "审核通过" : "已拒绝";
        return new ResultData(msg);
    }

    @GetMapping("/search")
    public ResultData search(HouseQueryDTO dto,
                             @RequestAttribute Integer userId,
                             @RequestAttribute Integer userType) {
        IPage<HouseListVO> page = houseService.searchHouses(dto, userType);
        return new ResultData(
                new String[]{"records", "total", "current", "size"},
                new Object[]{page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()},
                "查询成功"
        );
    }

    @GetMapping("/detail/{id}")
    public ResultData detail(@PathVariable Integer id,
                             @RequestAttribute(required = false) Integer userId,
                             @RequestAttribute(required = false) Integer userType) {
        HouseDetailVO vo = houseService.getDetailById(id, userId, userType);
        return new ResultData("data", vo, "查询成功");
    }
}
