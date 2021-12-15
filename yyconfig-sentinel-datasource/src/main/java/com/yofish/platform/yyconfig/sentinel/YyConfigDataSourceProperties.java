package com.yofish.platform.yyconfig.sentinel;

import com.alibaba.cloud.sentinel.datasource.config.AbstractDataSourceProperties;

import javax.validation.constraints.NotEmpty;

/**
 * YyConfigDataSourceProperties
 *
 * @author WangSongJun
 * @date 2021-12-06
 */
public class YyConfigDataSourceProperties extends AbstractDataSourceProperties {

    public YyConfigDataSourceProperties() {
        super(YyConfigDataSourceFactoryBean.class.getName());
    }

    @NotEmpty
    private String namespaceName;

    @NotEmpty
    private String flowRulesKey;

    private String defaultFlowRuleValue;

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    public String getFlowRulesKey() {
        return flowRulesKey;
    }

    public void setFlowRulesKey(String flowRulesKey) {
        this.flowRulesKey = flowRulesKey;
    }

    public String getDefaultFlowRuleValue() {
        return defaultFlowRuleValue;
    }

    public void setDefaultFlowRuleValue(String defaultFlowRuleValue) {
        this.defaultFlowRuleValue = defaultFlowRuleValue;
    }
}
