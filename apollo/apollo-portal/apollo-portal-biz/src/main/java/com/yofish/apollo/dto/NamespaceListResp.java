package com.yofish.apollo.dto;

import lombok.Data;

import javax.persistence.Entity;

/**
 * @author rache
 * @date 2019-12-25
 */
@Data
public class NamespaceListResp {

    private Long id;
    private String env;
    private String name;
}
