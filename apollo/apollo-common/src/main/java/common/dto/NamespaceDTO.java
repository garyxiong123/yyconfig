package common.dto;

import common.utils.InputValidator;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class NamespaceDTO extends BaseDTO {
    private long id;

    private Long appId;

    private String clusterName;

    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid Namespace format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    private String namespaceName;
}
