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
package common.dto;

import common.utils.InputValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@Builder
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

    private Integer instanceCount;

    public NamespaceDTO(String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime, long id, String appCode, String clusterName, String namespaceName, Integer instanceCount) {
        super(createAuthor, createTime, updateAuthor, updateTime);
        this.id = id;
        this.appCode = appCode;
        this.clusterName = clusterName;
        this.namespaceName = namespaceName;
        this.instanceCount = instanceCount;
    }
}
