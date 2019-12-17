package com.yofish.apollo.model.model;

import common.utils.InputValidator;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Data
public class AppNamespaceModel {

    @NotBlank(message = "name cannot be blank")
    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid name format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    private String name;

    private ConfigFileFormat format;

    private String comment;
}
