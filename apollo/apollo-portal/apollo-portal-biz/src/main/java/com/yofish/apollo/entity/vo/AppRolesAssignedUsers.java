package com.yofish.apollo.entity.vo;


import com.yofish.apollo.entity.bo.UserInfo;
import lombok.Data;

import java.util.Set;

@Data
public class AppRolesAssignedUsers {

  private String appId;
  private Set<UserInfo> masterUsers;
}
