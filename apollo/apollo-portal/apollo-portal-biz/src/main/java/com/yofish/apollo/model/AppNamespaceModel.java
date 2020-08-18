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

import com.yofish.apollo.domain.App;
import com.yofish.apollo.enums.AppNamespaceType;
import com.yofish.apollo.enums.NamespaceType;
import common.utils.InputValidator;
import framework.apollo.core.enums.ConfigFileFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Data
@ApiModel("命名空间")
public class AppNamespaceModel {

    @NotBlank(message = "name cannot be blank")
    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid name format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    @ApiModelProperty("命名空间名字")
    private String name;

    @ApiModelProperty("命名空间配置文件格式")
    private ConfigFileFormat format = ConfigFileFormat.Properties;

    @ApiModelProperty("备注")
    private String comment;

    @ApiModelProperty("授权的项目集合")
    private Set<App> authorizedApp;

    @ApiModelProperty("开放命名空间类型ID")
    private Long openNamespaceTypeId;

    private AppNamespaceType appNamespaceType;

    private long appId;

}
