package com.yofish.apollo.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.ServerConfig;
//import com.yofish.apollo.model.vo.Organization;
import com.yofish.apollo.repository.ServerConfigRepository;
import com.youyu.common.helper.YyRequestInfoHelper;
import common.config.RefreshableConfig;
import common.config.RefreshablePropertySource;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.Env;
import framework.apollo.tracer.Tracer;
import framework.foundation.Foundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class PortalConfig extends RefreshableConfig {

    private static final Logger logger = LoggerFactory.getLogger(PortalConfig.class);

    private static final int DEFAULT_ITEM_KEY_LENGTH = 128;
    private static final int DEFAULT_ITEM_VALUE_LENGTH = 20000;

    private static final String LIST_SEPARATOR = ",";

    @Autowired
    private ServerConfigRepository serverConfigRepository;
    @Autowired
    private ConfigurableEnvironment environment;
    private Gson gson = new Gson();
//    private static final Type ORGANIZATION = new TypeToken<List<Organization>>() {
//    }.getType();

//  @Autowired
//  private PortalDBPropertySource portalDBPropertySource;
    private static final Type namespaceValueLengthOverrideTypeReference =
        new TypeToken<Map<Long, Integer>>() {
        }.getType();


    public PortalConfig(String name, Map<String, Object> source) {
        super(name, source);
    }


    public PortalConfig() {
        super("DBConfig", Maps.newConcurrentMap());
    }

    @Override
    public List<RefreshablePropertySource> getRefreshablePropertySources() {
        return Collections.singletonList(null);
    }

    String getCurrentDataCenter() {
        return Foundation.server().getDataCenter();
    }

    protected void refresh() {
        Iterable<ServerConfig> dbConfigs = serverConfigRepository.findAll();

        Map<String, Object> newConfigs = Maps.newHashMap();
        //default appEnvCluster's configs
        /*for (ServerConfig config : dbConfigs) {
            if (Objects.equals(ConfigConsts.CLUSTER_NAME_DEFAULT, config.getAppEnvCluster())) {
                newConfigs.put(config.getKey(), config.getValue());
            }
        }

        //data center's configs
        String dataCenter = getCurrentDataCenter();
        for (ServerConfig config : dbConfigs) {
            if (Objects.equals(dataCenter, config.getAppEnvCluster())) {
                newConfigs.put(config.getKey(), config.getValue());
            }
        }

        //appEnvCluster's config
        if (!isNullOrEmpty(System.getProperty(ConfigConsts.APOLLO_CLUSTER_KEY))) {
            String appEnvCluster = System.getProperty(ConfigConsts.APOLLO_CLUSTER_KEY);
            for (ServerConfig config : dbConfigs) {
                if (Objects.equals(appEnvCluster, config.getAppEnvCluster())) {
                    newConfigs.put(config.getKey(), config.getValue());
                }
            }
        }
*/
        //put to environment
//        for (Map.Entry<String, Object> config : newConfigs.entrySet()) {
//            String key = config.getKey();
//            Object value = config.getValue();
//
//            if (this.source.get(key) == null) {
//                logger.info("Load config from DB : {} = {}", key, value);
//            } else if (!Objects.equals(this.source.get(key), value)) {
//                logger.info("Load config from DB : {} = {}. Old value = {}", key,
//                        value, this.source.get(key));
//            }
//
//            this.source.put(key, value);
//
//        }

    }


    public Set<Env> publishTipsSupportedEnvs() {
        String[] configurations = getArrayProperty("appNamespace.publish.tips.supported.envs", null);

        Set<Env> result = Sets.newHashSet();
        if (configurations == null || configurations.length == 0) {
            return result;
        }

        for (String env : configurations) {
            result.add(Env.fromString(env));
        }

        return result;
    }


    @Override
    public String[] getArrayProperty(String key, String[] defaultValue) {
        try {
            String value = getValue(key);
            return Strings.isNullOrEmpty(value) ? defaultValue : value.split(LIST_SEPARATOR);
        } catch (Throwable e) {
            Tracer.logError("Get array property failed.", e);
            return defaultValue;
        }
    }

    @Override
    public String getValue(String key, String defaultValue) {
        try {
            return environment.getProperty(key, defaultValue);
        } catch (Throwable e) {
            Tracer.logError("Get value failed.", e);
            return defaultValue;
        }
    }

    @Override
    public String getValue(String key) {
        return environment.getProperty(key);
    }

    @Override
    public int releaseMessageScanIntervalInMilli() {
        return 10000;
    }

    @Override
    public boolean isConfigServiceCacheEnabled() {
        return false;
    }


    /***
     * Level: important
     **/
    public List<Env> portalSupportedEnvs() {
        String[] configurations = getArrayProperty("apollo.portal.envs", new String[]{"FAT", "UAT", "PRO"});
        List<Env> envs = Lists.newLinkedList();

        for (String env : configurations) {
            envs.add(Env.fromString(env));
        }

        return envs;
    }

    public List<String> superAdmins() {
        String superAdminConfig = getValue("superAdmin", "");
        if (Strings.isNullOrEmpty(superAdminConfig)) {
            return Collections.emptyList();
        }
        return splitter.splitToList(superAdminConfig);
    }

    public Set<Env> emailSupportedEnvs() {
        String[] configurations = getArrayProperty("email.supported.envs", null);

        Set<Env> result = Sets.newHashSet();
        if (configurations == null || configurations.length == 0) {
            return result;
        }

        for (String env : configurations) {
            result.add(Env.fromString(env));
        }

        return result;
    }

    public boolean isConfigViewMemberOnly(String env) {
        String[] configViewMemberOnlyEnvs = getArrayProperty("configView.memberOnly.envs", new String[0]);

        for (String memberOnlyEnv : configViewMemberOnlyEnvs) {
            if (memberOnlyEnv.equalsIgnoreCase(env)) {
                return true;
            }
        }

        return false;
    }

    /***
     * Level: normal
     **/
    public int connectTimeout() {
        return getIntProperty("api.connectTimeout", 3000);
    }

    public int readTimeout() {
        return getIntProperty("api.readTimeout", 10000);
    }

/*
    public List<Organization> organizations() {

        String organizations = getValue("organizations");
        return organizations == null ? Collections.emptyList() : gson.fromJson(organizations, ORGANIZATION);
    }
*/

    public String portalAddress() {
        return getValue("apollo.portal.address");
    }

    public boolean isEmergencyPublishAllowed(Env env) {
        String targetEnv = env.name();

        String[] emergencyPublishSupportedEnvs = getArrayProperty("emergencyPublish.supported.envs", new String[0]);

        for (String supportedEnv : emergencyPublishSupportedEnvs) {
            if (Objects.equals(targetEnv, supportedEnv.toUpperCase().trim())) {
                return true;
            }
        }

        return false;
    }


    public String consumerTokenSalt() {
        return getValue("consumer.token.salt", "apollo-portal");
    }

    public String emailSender() {
        return getValue("email.sender");
    }

    public String emailTemplateFramework() {
        return getValue("email.template.framework", "");
    }

    public String emailReleaseDiffModuleTemplate() {
        return getValue("email.template.release.module.diff", "");
    }

    public String emailRollbackDiffModuleTemplate() {
        return getValue("email.template.rollback.module.diff", "");
    }

    public String emailGrayRulesModuleTemplate() {
        return getValue("email.template.release.module.rules", "");
    }

    public String wikiAddress() {
        return getValue("wiki.address", "https://github.com/ctripcorp/apollo/wiki");
    }

    public boolean canAppAdminCreatePrivateNamespace() {
        return getBooleanProperty("admin.createPrivateNamespace.switch", true);
    }

    /***
     * The following configurations are used in ctrip profile
     **/

    public int appId() {
        return getIntProperty("ctrip.appid", 0);
    }

    //send code & template id. apply from ewatch
    public String sendCode() {
        return getValue("ctrip.email.send.code");
    }

    public int templateId() {
        return getIntProperty("ctrip.email.template.id", 0);
    }

    //email retention time in email server queue.TimeUnit: hour
    public int survivalDuration() {
        return getIntProperty("ctrip.email.survival.duration", 5);
    }

    public boolean isSendEmailAsync() {
        return getBooleanProperty("email.send.async", true);
    }

    public String portalServerName() {
        return getValue("serverName");
    }

    public String casServerLoginUrl() {
        return getValue("casServerLoginUrl");
    }

    public String casServerUrlPrefix() {
        return getValue("casServerUrlPrefix");
    }

    public String credisServiceUrl() {
        return getValue("credisServiceUrl");
    }

    public String userServiceUrl() {
        return getValue("userService.url");
    }

    public String userServiceAccessToken() {
        return getValue("userService.accessToken");
    }

    public String soaServerAddress() {
        return getValue("soa.server.address");
    }

    public String cloggingUrl() {
        return getValue("clogging.server.url");
    }

    public String cloggingPort() {
        return getValue("clogging.server.port");
    }

    public String hermesServerAddress() {
        return getValue("hermes.server.address");
    }
    public int itemKeyLengthLimit() {
        int limit = getIntProperty("item.key.length.limit", DEFAULT_ITEM_KEY_LENGTH);
        return checkInt(limit, 5, Integer.MAX_VALUE, DEFAULT_ITEM_KEY_LENGTH);
    }

    public Map<Long, Integer> namespaceValueLengthLimitOverride() {
        String namespaceValueLengthOverrideString = getValue("appNamespace.value.length.limit.override");
        Map<Long, Integer> namespaceValueLengthOverride = Maps.newHashMap();
        if (!Strings.isNullOrEmpty(namespaceValueLengthOverrideString)) {
            namespaceValueLengthOverride =
                    gson.fromJson(namespaceValueLengthOverrideString, namespaceValueLengthOverrideTypeReference);
        }

        return namespaceValueLengthOverride;
    }
    public int itemValueLengthLimit() {
        int limit = getIntProperty("item.value.length.limit", DEFAULT_ITEM_VALUE_LENGTH);
        return checkInt(limit, 5, Integer.MAX_VALUE, DEFAULT_ITEM_VALUE_LENGTH);
    }


    public int releaseMessageCacheScanInterval() {
        return 0;
    }

    public TimeUnit releaseMessageCacheScanIntervalTimeUnit() {
        return null;
    }

    public int grayReleaseRuleScanInterval() {
        return 0;
    }

    public int releaseMessageNotificationBatch() {
        return 0;
    }

    public long releaseMessageNotificationBatchIntervalInMilli() {
        return 0;
    }

    public int checkInt(int someInvalidValue, int someMin, int maxValue, int someDefaultValue) {
        return 0;
    }

    public Object appNamespaceCacheRebuildInterval() {
        return null;
    }

    public Object appNamespaceCacheRebuildIntervalTimeUnit() {
        return null;
    }

    public Object appNamespaceCacheScanInterval() {
        return null;
    }

    public Object appNamespaceCacheScanIntervalTimeUnit() {
            return null;
    }

    public List<Env> getActiveEnvs() {
        return null;
    }
}
