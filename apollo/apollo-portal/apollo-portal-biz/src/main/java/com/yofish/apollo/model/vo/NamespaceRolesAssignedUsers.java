package com.yofish.apollo.model.vo;


import com.yofish.apollo.model.bo.UserInfo;
import lombok.Data;

import java.util.Set;

@Data
public class NamespaceRolesAssignedUsers {

    private String appId;
    private String namespaceName;

    private Set<UserInfo> modifyRoleUsers;
    private Set<UserInfo> releaseRoleUsers;

}
