package com.yofish.apollo.dto;

import common.dto.BaseDTO;
import lombok.Data;

/**
 * @author rache
 * @date 2019-12-25
 */
@Data
public class CommitDto extends BaseDTO {
    private String name;

    private Long appEnvClusterNamespace;

    private String changeSets;

    private String comment;

}
