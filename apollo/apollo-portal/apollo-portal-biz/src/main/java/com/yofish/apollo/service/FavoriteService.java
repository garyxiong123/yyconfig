/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Favorites;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.apollo.repository.FavoriteRepository;
import com.yofish.gary.biz.domain.User;
import com.youyu.common.api.Result;
import com.youyu.common.helper.YyRequestInfoHelper;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
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

//    public List<Favorite> search(String userId, String appCode, Pageable page) {
//        return null;
//    }

    public Result addOrCancelFavorite(String appCode) {

        Long userId = YyRequestInfoHelper.getCurrentUserId();
        if (null == userId) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Logger not be found");
        }

        App app = appRepository.findByAppCode(appCode);
        if (app == null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Application not be found");
        }

        User user = new User();
        user.setId(userId);

        Favorites favoriteInDB = favoriteRepository.findByUserAndApp(user, app);
        if (null == favoriteInDB) {
            // add favorite
            Favorites favorites = new Favorites();
            favorites.setUser(user);
            favorites.setApp(app);
            favoriteRepository.save(favorites);
            return Result.okDesc("add favorite success");
        } else {
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
