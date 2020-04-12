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
package com.yofish.apollo.controller;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.service.FavoriteService;
import com.youyu.common.api.Result;
import com.youyu.common.helper.YyRequestInfoHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;


    @RequestMapping(value = "/favorites", method = RequestMethod.GET)
    public Result addOrCancelFavorite(@RequestParam String appCode) {
        return favoriteService.addOrCancelFavorite(appCode);
    }


    @ApiOperation("用户收藏的项目")
    @GetMapping("favorites/app")
    public Result<List<App>> getUserFavoriteApps() {
        Long currentUserId = YyRequestInfoHelper.getCurrentUserId();
        List<App> favoriteAppByUser = favoriteService.findFavoriteAppByUser(currentUserId);
        return Result.ok(favoriteAppByUser);
    }
}
