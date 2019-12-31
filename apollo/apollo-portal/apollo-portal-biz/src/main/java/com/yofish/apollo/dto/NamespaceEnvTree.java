package com.yofish.apollo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author rache
 * @date 2019-12-31
 */
@Data
public class NamespaceEnvTree {
    private String env;
    private List<NamespaceListResp> namespaceListResps;
}
