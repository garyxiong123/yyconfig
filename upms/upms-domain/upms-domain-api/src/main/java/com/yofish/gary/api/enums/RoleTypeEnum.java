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
package com.yofish.gary.api.enums;

import lombok.Getter;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月28日 10:00:00
 * @work 角色类型枚举
 */
@Getter
public enum RoleTypeEnum {

    ADMIN(1L, "管理员"),
    ORDINARY_USER(2L, "普通用户");

    private Long code;

    private String desc;

    RoleTypeEnum(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
