package com.yofish.apollo.model;


import common.utils.InputValidator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
public class AppModel {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "appCode cannot be blank")
    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid appCode format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    private String appCode;

    @NotNull(message = "orgId cannot be blank")
    private Long orgId;


    @NotNull(message = "ownerId cannot be blank")
    private Long ownerId;

    private Set<Long> admins;
}
