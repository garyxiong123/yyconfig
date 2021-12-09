package com.yofish.platform.yyconfig.sentinel;

import com.alibaba.csp.sentinel.datasource.Converter;
import org.springframework.beans.factory.FactoryBean;

/**
 * YyConfigDataSourceFactoryBean
 *
 * @author WangSongJun
 * @date 2021-12-06
 */
public class YyConfigDataSourceFactoryBean implements FactoryBean<YyConfigDataSource> {

    private String namespaceName;

    private String flowRulesKey;

    private String defaultFlowRuleValue;

    private Converter converter;

    @Override
    public YyConfigDataSource getObject() throws Exception {
        return new YyConfigDataSource(namespaceName, flowRulesKey, defaultFlowRuleValue, converter);
    }

    @Override
    public Class<?> getObjectType() {
        return YyConfigDataSource.class;
    }

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

    public Converter getConverter() {
        return converter;
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }
}
