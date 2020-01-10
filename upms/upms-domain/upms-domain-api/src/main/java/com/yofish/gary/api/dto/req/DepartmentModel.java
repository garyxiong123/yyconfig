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
package com.yofish.gary.api.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author WangSongJun
 * @date 2019-12-09
 */
@Data
public class DepartmentModel {

    @NotBlank(message = "code cannot be blank")
    @Pattern(
            regexp = "[0-9a-zA-Z_.-]+",
            message = "Invalid code format: 只允许输入数字，字母和符号 - _ ."
    )
    private String code;

    @NotBlank(message = "name cannot be blank")
    private String name;

    private String comment;
}
