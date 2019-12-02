package com.yofish.apollo.model;


import common.utils.InputValidator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Data
public class AppModel {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "appId cannot be blank")
    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid AppId format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    private String appId;

    @NotBlank(message = "orgId cannot be blank")
    private String orgId;

    @NotBlank(message = "orgName cannot be blank")
    private String orgName;

    @NotBlank(message = "ownerName cannot be blank")
    private String ownerName;

    private Set<String> admins;
}
