package com.yofish.apollo.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {
/*

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
      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Username and password can not be empty.");
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

*/

}
