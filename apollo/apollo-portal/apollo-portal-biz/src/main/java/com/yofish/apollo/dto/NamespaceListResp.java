package com.yofish.apollo.dto;

import lombok.Data;

import javax.persistence.Entity;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rache
 * @date 2019-12-25
 */
@Data
public class NamespaceListResp{
    private Long id;
    private String env;
    private String name;
}
