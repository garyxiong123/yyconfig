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
package com.yofish.gary.api.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 角色查询响应rsp
 */
@ApiModel("角色查询响应rsp")
@Setter
@Getter
public class RoleQueryRspDTO implements Serializable {

    private static final long serialVersionUID = -532181330625338072L;

    @ApiModelProperty("角色id")
    private Long roleId;

    @ApiModelProperty("角色名")
    private String roleName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("更新者")
    private String updateAuthor;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("角色权限id列表")
    private List<String> permissionIds = new ArrayList<>();
}
