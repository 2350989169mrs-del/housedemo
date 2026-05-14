package com.jinsi.housedemo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinsi.housedemo.entity.Favorite;
import com.jinsi.housedemo.entity.FavoriteVO;
import com.jinsi.housedemo.service.IFavoriteService;
import com.jinsi.housedemo.util.ResultData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private IFavoriteService favoriteService;

    @GetMapping("/list")
    public ResultData list() {
        List<Favorite> list = favoriteService.list();
        return new ResultData("data", list, "查询成功");
    }

    @GetMapping("/page")
    public ResultData page(@RequestParam(defaultValue = "1") Integer current,
                          @RequestParam(defaultValue = "10") Integer size) {
        Page<Favorite> page = new Page<>(current, size);
        Page<Favorite> result = favoriteService.page(page);
        return new ResultData("data", result, "查询成功");
    }

    @GetMapping("/{id}")
    public ResultData getById(@PathVariable Integer id) {
        Favorite favorite = favoriteService.getById(id);
        if (favorite != null) {
            return new ResultData("data", favorite, "查询成功");
        }
        return new ResultData(404, "数据不存在");
    }

    // ==================== 添加收藏 ====================

    @PostMapping("/add")
    public ResultData add(@RequestAttribute Integer userId,
                          @RequestParam Integer houseId) {
        favoriteService.addFavorite(userId, houseId);
        return new ResultData("收藏成功");
    }

    // ==================== 取消收藏 ====================

    @DeleteMapping("/remove")
    public ResultData remove(@RequestAttribute Integer userId,
                             @RequestParam Integer houseId) {
        favoriteService.removeFavorite(userId, houseId);
        return new ResultData("已取消收藏");
    }

    // ==================== 我的收藏列表 ====================

    @GetMapping("/my-list")
    public ResultData myList(@RequestAttribute Integer userId) {
        List<FavoriteVO> list = favoriteService.listMyFavorites(userId);
        return new ResultData("data", list, "查询成功");
    }

    // ==================== 判断是否收藏 ====================

    @GetMapping("/is-favorite")
    public ResultData isFavorite(@RequestAttribute Integer userId,
                                 @RequestParam Integer houseId) {
        boolean fav = favoriteService.isFavorite(userId, houseId);
        return new ResultData("data", fav, "查询成功");
    }


    @PutMapping
    public ResultData update(@RequestBody Favorite favorite) {
        boolean success = favoriteService.updateById(favorite);
        if (success) {
            return new ResultData("修改成功");
        }
        return new ResultData(500, "修改失败");
    }

    @DeleteMapping("/{id}")
    public ResultData delete(@PathVariable Integer id) {
        boolean success = favoriteService.removeById(id);
        if (success) {
            return new ResultData("删除成功");
        }
        return new ResultData(500, "删除失败");
    }
}