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

import com.yofish.gary.api.page.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户查询请求req
 */
@ApiModel("用户查询请求req")
@Setter
@Getter
public class UserQueryReqDTO extends PageQuery {

    private static final long serialVersionUID = 4270708928228954721L;

    @ApiModelProperty("用户id")
    private Long id;

    @ApiModelProperty("查询条件(模糊查询):用户名,真实姓名,手机号,邮箱")
    private String condition;

    @ApiModelProperty("状态")
    private String status;
}
