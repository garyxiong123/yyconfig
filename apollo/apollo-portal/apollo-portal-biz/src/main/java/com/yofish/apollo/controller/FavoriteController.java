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
//                                      @RequestParam(value = "appId", required = false) String appId,
//                                      Pageable page) {
//    return favoriteService.search(userId, appId, page);
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
