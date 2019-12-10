package com.yofish.apollo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author WangSongJun
 * @date 2019-12-10
 */
@Getter
@AllArgsConstructor
public enum ServerConfigKey {

    /**
     * 可支持的环境列表
     */
    ApolloPortalEnvs("apollo.portal.envs"),

    /**
     * Portal超级管理员
     */
    SuperAdmin("super.admin"),

    /**
     * http接口read timeout
     */
    ApiReadTimeout("api.read.timeout"),

    /**
     * consumer token salt
     */
    ConsumerTokenSalt("consumer.token.salt"),

    /**
     * 是否允许项目管理员创建私有namespace
     */
    AdminCreatePrivateNamespaceSwitch("admin.createPrivateNamespace.switch"),

    /**
     * 只对项目成员显示配置信息的环境列表，多个env以英文逗号分隔
     */
    ConfigViewMemberOnlyEnvs("configView.memberOnly.envs");

    private String key;
}
