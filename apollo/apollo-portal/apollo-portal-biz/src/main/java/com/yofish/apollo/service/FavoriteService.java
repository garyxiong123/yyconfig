package com.yofish.apollo.service;

import com.yofish.apollo.controller.Favorite;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/6 上午10:09
 */
@Service
public class FavoriteService {
    public List<Favorite> search(String userId, String appId, Pageable page) {
        return null;
    }

    public Favorite addFavorite(Favorite favorite) {
        return null;

    }

    public void deleteFavorite(long favoriteId) {

    }

    public void adjustFavoriteToFirst(long favoriteId) {

    }
}
