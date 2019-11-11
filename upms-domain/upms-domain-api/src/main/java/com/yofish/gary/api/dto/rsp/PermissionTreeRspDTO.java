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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限树响应rsp
 */
@ApiModel("权限树响应rsp")
@Setter
@Getter
public class PermissionTreeRspDTO implements Serializable {

    private static final long serialVersionUID = 5683051604723729884L;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("url地址")
    private String path;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("子权限")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PermissionTreeRspDTO> childrenTree = new ArrayList<>();
}
