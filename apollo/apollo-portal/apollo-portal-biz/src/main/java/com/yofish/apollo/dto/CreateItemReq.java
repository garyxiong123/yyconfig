package com.yofish.apollo.dto;

import com.yofish.apollo.domain.Item;
import lombok.Data;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/18 下午3:04
 */
@Data
public class CreateItemReq {
    private String appId;
    private String env;
    private String clusterName;
    private String namespaceName;
    private String key;

    private String value;

    private String comment;
    private Long appEnvClusterNamespaceId;
    private Integer lineNum;
    private String type;
}
