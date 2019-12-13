package com.yofish.apollo.dto;

import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Data;

/**
 * @author rache
 * @date 2019-12-10
 */
@Data
public class ModifyItemsByTextsReq {
    private String appId;
    private String env;
    private String clusterName;
    private String namespaceName;
    private long namespaceId;
    private ConfigFileFormat format;
    private String configText;

}
