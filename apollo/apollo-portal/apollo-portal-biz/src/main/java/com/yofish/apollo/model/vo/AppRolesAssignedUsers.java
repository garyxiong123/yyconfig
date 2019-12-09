package com.yofish.apollo.model.vo;


import com.yofish.apollo.model.bo.UserInfo;
import lombok.Data;

import java.util.Set;

@Data
public class AppRolesAssignedUsers {

  private String appId;
  private Set<UserInfo> masterUsers;
}
