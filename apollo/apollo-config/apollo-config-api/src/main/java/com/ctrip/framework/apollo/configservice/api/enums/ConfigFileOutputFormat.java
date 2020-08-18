package com.ctrip.framework.apollo.configservice.api.enums;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/4/16 上午11:58
 */
public enum ConfigFileOutputFormat {
    PROPERTIES("properties"), JSON("json");

    private String value;

    ConfigFileOutputFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
