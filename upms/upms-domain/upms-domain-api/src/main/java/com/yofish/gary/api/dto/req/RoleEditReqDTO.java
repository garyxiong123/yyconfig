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
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 角色编辑请求req
 */
@ApiModel("角色编辑请求req")
@Setter
@Getter
public class RoleEditReqDTO implements Serializable {

    private static final long serialVersionUID = 1711668166352732601L;

    @ApiModelProperty("角色id")
    @NotNull
    private Long roleId;

    @ApiModelProperty("角色名")
    @NotNull
    private String roleName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("权限列表")
    private List<Long> permissionIds;
}
