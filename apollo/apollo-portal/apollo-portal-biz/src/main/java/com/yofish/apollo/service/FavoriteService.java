package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Favorites;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.apollo.repository.FavoriteRepository;
import com.yofish.gary.biz.domain.User;
import com.youyu.common.api.Result;
import com.youyu.common.helper.YyRequestInfoHelper;
import common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/6 上午10:09
 */
@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private AppRepository appRepository;

//    public List<Favorite> search(String userId, String appId, Pageable page) {
//        return null;
//    }

    public Result addOrCancelFavorite(String appCode) {

        Long userId = YyRequestInfoHelper.getCurrentUserId();
        if (null == userId){
            throw new BadRequestException("Logger not be found");
        }
        
        App app = appRepository.findByAppCode(appCode);
        if (app == null){
            throw new BadRequestException("Application not be found");
        }

        List<App> apps = new ArrayList<>();
        apps.add(app);

        User user = new User();
        user.setId(userId);

        Favorites favoriteInDB = favoriteRepository.findByUserAndApps(user,apps);

        if (null == favoriteInDB){
            // add favorite
            Favorites favorites = new Favorites();
            favorites.setUser(user);
            favorites.setApps(apps);
            favoriteRepository.save(favorites);
            return Result.okDesc("add favorite success");
        }else{
            // delete favorite
            favoriteRepository.delete(favoriteInDB);
            return Result.okDesc("cancel favorite success");
        }
    }
//
//    public void deleteFavorite(long favoriteId) {
//
//    }
//
//    public void adjustFavoriteToFirst(long favoriteId) {
//
//    }
}
