package com.yofish.apollo.service;

import com.yofish.apollo.controller.Favorite;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/6 上午10:09
 */
public class FavoriteService {
    public List<Favorite> search(String userId, String appId, Pageable page) {
    }

    public Favorite addFavorite(Favorite favorite) {
    }

    public void deleteFavorite(long favoriteId) {
    }

    public void adjustFavoriteToFirst(long favoriteId) {
    }
}
