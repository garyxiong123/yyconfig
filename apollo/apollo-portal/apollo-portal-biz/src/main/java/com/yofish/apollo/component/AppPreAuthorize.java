package com.yofish.apollo.component;

import java.lang.annotation.*;

/**
 * 项目的预授权
 *
 * @author WangSongJun
 * @date 2019-12-25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppPreAuthorize {
    Authorize value() default Authorize.AppDepartment;

    /**
     * 授权类型
     */
    enum Authorize {
        /**
         * 超级管理员
         */
        SuperAdmin,
        /**
         * 项目的拥有者
         */
        AppOwner,

        /**
         * 项目的负责人（参与人）
         */
        AppAdmin,

        /**
         * 项目的同部门
         */
        AppDepartment
    }
}
