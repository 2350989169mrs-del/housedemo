package com.jinsi.housedemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinsi.housedemo.entity.Favorite;
import com.jinsi.housedemo.entity.FavoriteVO;

import java.util.List;

public interface IFavoriteService extends IService<Favorite> {
    /** 添加收藏 */
    void addFavorite(Integer userId, Integer houseId);
    /** 取消收藏 */
    void removeFavorite(Integer userId, Integer houseId);
    /** 我的收藏列表 */
    List<FavoriteVO> listMyFavorites(Integer userId);
    /** 判断是否已收藏 */
    boolean isFavorite(Integer userId, Integer houseId);
}
