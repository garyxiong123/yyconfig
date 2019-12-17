package com.yofish.apollo.dto;

import lombok.Data;

/**
 * @author rache
 * @date 2019-12-11
 */
@Data
public class ItemReq {
    private Long itemId;
    private Long clusterNamespaceId;
    private String appId;
    private String env;
    private String clusterName;
    private String namespaceName;
    private String orderBy;

}
