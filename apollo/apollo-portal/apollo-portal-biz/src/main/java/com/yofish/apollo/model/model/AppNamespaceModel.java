package com.yofish.apollo.model.model;

import com.yofish.apollo.domain.App;
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

}
