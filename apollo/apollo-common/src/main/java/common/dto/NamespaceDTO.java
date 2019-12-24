package common.dto;

import common.utils.InputValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NamespaceDTO extends BaseDTO {
    private long id;

    private String appCode;

    private String clusterName;

    @Pattern(
            regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
            message = "Invalid Namespace format: " + InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
    )
    private String namespaceName;

    public NamespaceDTO(String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime, long id, String appCode, String clusterName, String namespaceName) {
        super(createAuthor, createTime, updateAuthor, updateTime);
        this.id = id;
        this.appCode = appCode;
        this.clusterName = clusterName;
        this.namespaceName = namespaceName;
    }
}
