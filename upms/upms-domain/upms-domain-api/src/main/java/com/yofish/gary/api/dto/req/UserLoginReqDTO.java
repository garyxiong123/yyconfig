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
package com.yofish.gary.api.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户登录请求req
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("用户登录请求req")
@Data
public class UserLoginReqDTO implements Serializable {

    private static final long serialVersionUID = 7980211784973171962L;

    @ApiModelProperty("用户名")
    @NotNull
    private String username;

    @ApiModelProperty("密码")
    @NotNull
    private String password;
}
