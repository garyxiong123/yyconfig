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

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限查询响应rsp
 */
@ApiModel("权限查询响应rsp")
@Setter
@Getter
public class PermissionQueryRspDTO implements Serializable {

    private static final long serialVersionUID = -986193000948442789L;

    @ApiModelProperty("权限id")
    private Long permissionId;

    @ApiModelProperty("权限编码:默认值sys")
    private String code;

    @ApiModelProperty("权限名称")
    private String permissionName;

    @ApiModelProperty("权限url")
    private String url;

    @ApiModelProperty("权限类型")
    private Integer type;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("父级权限:默认值0代表根权限")
    private Long parentId;

    @ApiModelProperty("排序")
    private Integer rank;

    @ApiModelProperty("iframe地址")
    private String iframeUrl;

    @ApiModelProperty("iframe数据")
    private String iframeJson;

    @ApiModelProperty("创建者")
    private String createAuthor;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
