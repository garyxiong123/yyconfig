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
package com.yofish.apollo.model.model;


import common.utils.InputValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppModel {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "appCode cannot be blank")
    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid appCode format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    private String appCode;

    @NotNull(message = "orgId cannot be blank")
    private Long orgId;


    @NotNull(message = "ownerId cannot be blank")
    private Long ownerId;

    private Set<Long> admins;
}
