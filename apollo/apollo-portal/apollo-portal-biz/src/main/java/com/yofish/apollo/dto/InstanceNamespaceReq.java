package com.yofish.apollo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author rache
 * @date 2019-12-19
 */
@Data
public class InstanceNamespaceReq implements Serializable {
    private Long releaseId;
    private Long namespaceId;
}
