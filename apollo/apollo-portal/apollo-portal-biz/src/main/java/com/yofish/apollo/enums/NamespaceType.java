package com.yofish.apollo.enums;

/**
 * @author WangSongJun
 * @date 2019-12-10
 */
public enum NamespaceType {
    /**
     * 公共的
     */
    Public,

    /**
     * 保护的，需授权
     */
    Protect,

    /**
     * 项目私有的
     */
    Private,

    /**
     * 关联的，覆盖公共的
     */
    Associate,
}
