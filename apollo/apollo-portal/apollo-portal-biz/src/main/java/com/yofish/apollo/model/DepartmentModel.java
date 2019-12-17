package com.yofish.apollo.model;

import common.utils.InputValidator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author WangSongJun
 * @date 2019-12-09
 */
@Data
public class DepartmentModel {

    @NotBlank(message = "code cannot be blank")
    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid code format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    private String code;

    @NotBlank(message = "name cannot be blank")
    private String name;

    private String comment;
}
