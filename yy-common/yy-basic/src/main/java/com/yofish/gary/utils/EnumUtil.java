/*
 *    Copyright 2018-2019 the original author or authors.
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
package com.yofish.gary.utils;

import com.youyu.common.api.IBaseResultCode;

import java.util.List;

import static org.apache.commons.lang3.EnumUtils.getEnumList;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月28日 10:00:00
 * @work 枚举工具类
 */
public class EnumUtil {

    /**
     * 根据code获取tClass对应的枚举类
     *
     * @param tClass
     * @param code
     * @param <T>
     * @return
     */
    public static <T extends Enum> T getEnum(Class<T> tClass, String code) {
        List<T> enums = getEnumList(tClass);
        for (T tEnum : enums) {
            IBaseResultCode iBaseResultCode = (IBaseResultCode) tEnum;
            if (StringUtil.eq(iBaseResultCode.getCode(), code)) {
                return tEnum;
            }
        }
        throw new RuntimeException("根据code:" + code + "获取" + tClass.getSimpleName() + "对应的枚举异常!");
    }
}
