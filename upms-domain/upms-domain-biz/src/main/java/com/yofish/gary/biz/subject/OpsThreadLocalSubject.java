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
package com.yofish.gary.biz.subject;


import com.yofish.gary.biz.subject.dto.OpsUserDto;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年8月6日 10:00:00
 * @work ops本地线程主体
 */
public class OpsThreadLocalSubject {

    /**
     * ops用户本地线程对象
     */
    private static final ThreadLocal<OpsUserDto> OPS_USER_THREAD_LOCAL = new ThreadLocal<OpsUserDto>() {
        @Override
        protected OpsUserDto initialValue() {
            return null;
        }
    };

    /**
     * 获取ops用户信息
     *
     * @return
     */
    public static OpsUserDto getOpsUser() {
        OpsUserDto opsUserDto = OPS_USER_THREAD_LOCAL.get();
        return opsUserDto;
    }

    /**
     * 设置ops用户信息
     *
     * @param opsUserDto
     */
    public static void setOpsUser(OpsUserDto opsUserDto) {
        OPS_USER_THREAD_LOCAL.set(opsUserDto);
    }

    /**
     * 移除ops用户信息
     */
    public static void removeOpsUser() {
        OPS_USER_THREAD_LOCAL.remove();
    }

    /**
     * 移除所有本地线程变量
     */
    public static void removeAll() {
        removeOpsUser();
    }
}
