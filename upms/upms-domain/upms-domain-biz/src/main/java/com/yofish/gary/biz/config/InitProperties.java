package com.yofish.gary.biz.config;

import lombok.Data;

/**
 * @author WangSongJun
 * @date 2020-01-03
 */
@Data
public class InitProperties {
    private boolean initData = true;

    private String department = "默认部门";
    private String departmentCode = "DefaultDepartment";
    private String departmentComment = "系统初始化默认部门";

    private String adminUsername = "apollo";
    private String adminPassword = "apollo";
    private String adminRealName = "管理员用户";
    private String adminEmail = "apollo@yofish.com";
}
