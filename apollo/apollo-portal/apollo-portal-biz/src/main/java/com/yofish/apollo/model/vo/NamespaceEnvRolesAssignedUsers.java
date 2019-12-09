package com.yofish.apollo.model.vo;


import framework.apollo.core.enums.Env;
import lombok.Data;

@Data
public class NamespaceEnvRolesAssignedUsers extends NamespaceRolesAssignedUsers {
    private Env env;
}
