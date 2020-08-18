/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.apollo.enums;

import com.yofish.apollo.domain.AppNamespace;

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
//        if (t instanceof AppNamespace4Private) {
//            return Private;
//        } else if (t instanceof AppNamespace4Protect) {
//            return Protect;
//        } else if (t instanceof AppNamespace4Public) {
//            return Public;
//        } else {
//            return null;
//        }
        return null;

    }
}
