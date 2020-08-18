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
package com.yofish.apollo.model;

import com.yofish.apollo.component.config.ServerConfigKey;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author WangSongJun
 * @date 2020-01-06
 */
@Data
public class ServerConfigModel {
    @NotNull(message = "key  cannot be null")
    private ServerConfigKey key;


    @NotBlank(message = "value cannot be blank")
    private String value;


    @NotBlank(message = "comment cannot be blank")
    private String comment;
}
