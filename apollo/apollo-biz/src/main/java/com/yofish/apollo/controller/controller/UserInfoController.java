package com.yofish.apollo.controller.controller;

import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.config.LogoutHandler;
import com.ctrip.framework.apollo.config.UserInfoHolder;
import com.ctrip.framework.apollo.config.UserService;
import com.ctrip.framework.apollo.config.springsecurity.SpringSecurityUserService;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.model.bo.UserInfo;
import com.ctrip.framework.apollo.model.entity.UserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class UserInfoController {

  @Autowired
  private UserInfoHolder userInfoHolder;
  @Autowired
  private LogoutHandler logoutHandler;

  @Autowired
  private UserService userService;


  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @RequestMapping(value = "/users", method = RequestMethod.POST)
  public void createOrUpdateUser(@RequestBody UserPO user) {
    if (StringUtils.isContainEmpty(user.getUsername(), user.getPassword())) {
      throw new BadRequestException("Username and password can not be empty.");
    }

    if (userService instanceof SpringSecurityUserService) {
      ((SpringSecurityUserService) userService).createOrUpdate(user);
    } else {
      throw new UnsupportedOperationException("Create or update user operation is unsupported");
    }

  }

  @RequestMapping(value = "/user", method = RequestMethod.GET)
  public UserInfo getCurrentUserName() {
    return userInfoHolder.getUser();
  }

  @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
  public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
    logoutHandler.logout(request, response);
  }

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public List<UserInfo> searchUsersByKeyword(@RequestParam(value = "keyword") String keyword,
                                             @RequestParam(value = "offset", defaultValue = "0") int offset,
                                             @RequestParam(value = "limit", defaultValue = "10") int limit) {
    return userService.searchUsers(keyword, offset, limit);
  }

  @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
  public UserInfo getUserByUserId(@PathVariable String userId) {
    return userService.findByUserId(userId);
  }


}
