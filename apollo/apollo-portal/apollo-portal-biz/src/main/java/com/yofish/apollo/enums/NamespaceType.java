package com.yofish.apollo.enums;

import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.AppNamespace4Private;
import com.yofish.apollo.domain.AppNamespace4Protect;
import com.yofish.apollo.domain.AppNamespace4Public;

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
    Associate;

    public static <T extends AppNamespace> NamespaceType getNamespaceTypeByInstance(T t) {
        if (t instanceof AppNamespace4Private) {
            return Private;
        } else if (t instanceof AppNamespace4Protect) {
            return Protect;
        } else if (t instanceof AppNamespace4Public) {
            return Public;
        } else {
            return null;
        }
    }
}
