package com.yofish.apollo.entity.vo;


import com.yofish.apollo.entity.bo.UserInfo;
import lombok.Data;

import java.util.Set;

@Data
public class NamespaceRolesAssignedUsers {

    private String appId;
    private String namespaceName;

    private Set<UserInfo> modifyRoleUsers;
    private Set<UserInfo> releaseRoleUsers;

}
