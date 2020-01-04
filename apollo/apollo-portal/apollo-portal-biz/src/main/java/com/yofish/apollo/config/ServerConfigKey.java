package com.yofish.apollo.config;


/**
 * 服务的配置项，可动态配置
 *
 * @author WangSongJun
 * @date 2019-12-10
 */
public enum ServerConfigKey {
    /**
     * 可支持的环境列表
     */
    APOLLO_PORTAL_ENVS,

    /**
     * http接口read timeout
     */
    API_READ_TIMEOUT,

    /**
     * consumer token salt
     */
    CONSUMER_TOKEN_SALT,

    /**
     * 是否允许项目管理员创建私有namespace
     */
    ADMIN_CREATE_PRIVATE_NAMESPACE_SWITCH,

    /**
     * 只对项目成员显示配置信息的环境列表，多个env以英文逗号分隔
     */
    CONFIG_VIEW_MEMBER_ONLY_ENVS;

}
