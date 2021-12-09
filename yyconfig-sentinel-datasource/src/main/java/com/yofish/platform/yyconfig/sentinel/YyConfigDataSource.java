package com.yofish.platform.yyconfig.sentinel;

import com.alibaba.csp.sentinel.datasource.AbstractDataSource;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.yofish.yyconfig.client.domain.config.Config;
import com.yofish.yyconfig.client.lifecycle.preboot.ConfigService;
import com.yofish.yyconfig.client.pattern.listener.config.ConfigChange;
import com.yofish.yyconfig.client.pattern.listener.config.ConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * YyConfigDataSource
 *
 * @author WangSongJun
 * @date 2021-12-06
 */
public class YyConfigDataSource<T> extends AbstractDataSource<String, T> {
    private static final Logger logger = LoggerFactory.getLogger(YyConfigDataSource.class);
    private final Config config;
    private final String ruleKey;
    private final String defaultRuleValue;
    private ConfigChangeListener configChangeListener;


    public YyConfigDataSource(String namespaceName, String ruleKey, String defaultRuleValue, Converter<String, T> parser) {
        super(parser);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(namespaceName), "Namespace name could not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(ruleKey), "RuleKey could not be null or empty!");
        this.ruleKey = ruleKey;
        this.defaultRuleValue = defaultRuleValue;
        this.config = ConfigService.getConfig(namespaceName);
        this.initialize();
        logger.info(String.format("Initialized rule for namespace: %s, rule key: %s", namespaceName, ruleKey), new Object[0]);

    }

    private void initialize() {
        this.initializeConfigChangeListener();
        this.loadAndUpdateRules();
    }

    private void loadAndUpdateRules() {
        try {
            T newValue = this.loadConfig();
            if (newValue == null) {
                logger.warn("[YyConfigDataSource] WARN: rule config is null, you may have to check your data source", new Object[0]);
            }

            this.getProperty().updateValue(newValue);
        } catch (Throwable var2) {
            logger.warn("[YyConfigDataSource] Error when loading rule config", var2);
        }

    }

    private void initializeConfigChangeListener() {
        this.configChangeListener = changeEvent -> {
            ConfigChange change = changeEvent.getChange(YyConfigDataSource.this.ruleKey);
            if (change != null) {
                logger.info("[YyConfigDataSource] Received config changes: " + change.toString(), new Object[0]);
            }

            YyConfigDataSource.this.loadAndUpdateRules();
        };
        this.config.addChangeListener(this.configChangeListener, Sets.newHashSet(new String[]{this.ruleKey}));
    }

    @Override
    public String readSource() throws Exception {
        return this.config.getProperty(this.ruleKey, this.defaultRuleValue);
    }

    @Override
    public void close() throws Exception {
        this.config.removeChangeListener(this.configChangeListener);
    }
}
