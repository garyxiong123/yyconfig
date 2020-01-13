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

import com.yofish.apollo.domain.Favorites;
import com.yofish.apollo.service.FavoriteService;
import com.youyu.common.api.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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


//  @RequestMapping(value = "/favorites", method = RequestMethod.GET)
//  public List<Favorite> findFavorites(@RequestParam(value = "userId", required = false) String userId,
//                                      @RequestParam(value = "appCode", required = false) String appCode,
//                                      Pageable page) {
//    return favoriteService.search(userId, appCode, page);
//  }
//
//
//  @RequestMapping(value = "/favorites/{favoriteId}", method = RequestMethod.DELETE)
//  public void deleteFavorite(@PathVariable long favoriteId) {
//    favoriteService.deleteFavorite(favoriteId);
//  }
//
//
//  @RequestMapping(value = "/favorites/{favoriteId}", method = RequestMethod.PUT)
//  public void toTop(@PathVariable long favoriteId) {
//    favoriteService.adjustFavoriteToFirst(favoriteId);
//  }

}
