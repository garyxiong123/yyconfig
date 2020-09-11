/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.yyconfig.client.repository;

import com.yofish.yyconfig.client.enums.ConfigSourceType;
import com.yofish.yyconfig.client.component.exceptions.ApolloConfigException;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.ApolloInjector;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ClientConfig;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ConfigServiceLocator;
import com.yofish.yyconfig.client.timer.VersionMonitor;
import com.yofish.yyconfig.client.component.util.http.HttpRequest;
import com.yofish.yyconfig.client.component.util.http.HttpResponse;
import com.yofish.yyconfig.client.component.util.http.HttpUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.yofish.yyconfig.common.framework.apollo.Apollo;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceConfig;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;
import com.yofish.yyconfig.common.framework.apollo.core.dto.ServiceDTO;
import com.yofish.yyconfig.common.framework.apollo.core.schedule.ExponentialSchedulePolicy;
import com.yofish.yyconfig.common.framework.apollo.core.schedule.SchedulePolicy;
import com.yofish.yyconfig.common.framework.apollo.core.utils.ApolloThreadFactory;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Jason Song(song_s@ctrip.com)  优化整个查询的链路
 * <p>
 * 开辟一片内存， 存储该namespace 对应的信息，
 * 1：client内部数据从这片内存里面获取
 * 2： 内存数据更新，通过定时任务去远程服务拉取版本，进行更新
 * 3：只管理该namespace的数据，  一个namespace一个线程？
 */
@Data
public class RemoteConfigRepository extends AbstractConfigRepository {
    private static final Logger logger = LoggerFactory.getLogger(RemoteConfigRepository.class);
    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);
    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");
    private static final Escaper pathEscaper = UrlEscapers.urlPathSegmentEscaper();
    private static final Escaper queryParamEscaper = UrlEscapers.urlFormParameterEscaper();

    @Autowired
    private ConfigServiceLocator m_serviceLocator;
    @Autowired
    private HttpUtil m_httpUtil;
    @Autowired
    private ClientConfig m_Client_config;
    @Autowired
    private VersionMonitor versionMonitor;
    private final String namespaceName;
    private final static ScheduledExecutorService m_executorService;
    private final AtomicReference<ServiceDTO> m_longPollServiceDto;
    private final RateLimiter m_loadConfigRateLimiter;
    private final AtomicBoolean m_configNeedForceRefresh;
    private final SchedulePolicy m_loadConfigFailSchedulePolicy;
    private final Gson gson;

    private final AtomicReference<LongNamespaceVersion> longNamespaceVersion;
    private volatile AtomicReference<NamespaceConfig> namespaceConfigCache;


    static {
        m_executorService = Executors.newScheduledThreadPool(1, ApolloThreadFactory.create("RemoteConfigRepository", true));
    }

    /**
     * Constructor.
     *
     * @param namespace the appNamespace
     */
    public RemoteConfigRepository(String namespace) {
        namespaceName = namespace;
        namespaceConfigCache = new AtomicReference<>();
        m_Client_config = ApolloInjector.getInstance(ClientConfig.class);
        m_httpUtil = ApolloInjector.getInstance(HttpUtil.class);
        m_serviceLocator = ApolloInjector.getInstance(ConfigServiceLocator.class);//通过Feign请求去处理
        versionMonitor = ApolloInjector.getInstance(VersionMonitor.class);
        m_longPollServiceDto = new AtomicReference<>();
        longNamespaceVersion = new AtomicReference<>();
        m_loadConfigRateLimiter = RateLimiter.create(m_Client_config.getLoadConfigQPS());
        m_configNeedForceRefresh = new AtomicBoolean(true);
        m_loadConfigFailSchedulePolicy = new ExponentialSchedulePolicy(m_Client_config.getOnErrorRetryInterval(), m_Client_config.getOnErrorRetryInterval() * 8);
        gson = new Gson();
        this.trySync();//从config拉去namespace配置到本地
        this.schedulePeriodicRefresh();//定时 从config拉去namespace配置到本地
        this.add2VersionControl(); //提交给 长连接版本控制
    }

    @Override
    public Properties getConfig() {
        if (namespaceConfigCache.get() == null) {
            this.sync();
        }
        return transformApolloConfigToProperties(namespaceConfigCache.get());
    }

    @Override
    public void setUpstreamRepository(ConfigRepository upstreamConfigRepository) {
        //remote config doesn't need upstream
    }

    @Override
    public ConfigSourceType getSourceType() {
        return ConfigSourceType.REMOTE;
    }

    /**
     * 定时同步配置
     */
    private void schedulePeriodicRefresh() {
        logger.debug("Schedule periodic refresh with interval: {} {}", m_Client_config.getRefreshInterval(), m_Client_config.getRefreshIntervalTimeUnit());

        m_executorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        Tracer.logEvent("Apollo.ConfigService", String.format("periodicRefresh: %s", namespaceName));
                        logger.debug("refresh config for appNamespace: {}", namespaceName);
                        trySync();
                        Tracer.logEvent("Apollo.Client.Version", Apollo.VERSION);
                    }
                }, m_Client_config.getRefreshInterval(), m_Client_config.getRefreshInterval(),
                m_Client_config.getRefreshIntervalTimeUnit());
    }

    @Override
    protected synchronized void sync() {
        NamespaceConfig previous = namespaceConfigCache.get() == null ? new NamespaceConfig() : namespaceConfigCache.get();
        NamespaceConfig current = loadRemoteNamespaceConfig();

        //reference equals means HTTP 304
        if (previous.getReleaseKey() != current.getReleaseKey()) {
            logger.debug("Remote Config refreshed!");
            namespaceConfigCache.set(current);
            this.fireRepositoryChange(namespaceName, this.getConfig());
        }

        if (current != null) {
            Tracer.logEvent(String.format("Apollo.Client.Configs.%s", current.getNamespaceName()), current.getReleaseKey());
        }

    }

    private Properties transformApolloConfigToProperties(NamespaceConfig namespaceConfig) {
        Properties result = new Properties();
        result.putAll(namespaceConfig.getConfigurations());
        return result;
    }


    //TODO fix it to feign 读取新的配置
    private NamespaceConfig loadRemoteNamespaceConfig() {


        String url = assembleQueryConfigUrl(namespaceName);

        logger.debug("Loading config from {}", url);
        HttpRequest request = new HttpRequest(url);

        HttpResponse<NamespaceConfig> response = m_httpUtil.doGet(request, NamespaceConfig.class);
        m_configNeedForceRefresh.set(false);
        m_loadConfigFailSchedulePolicy.success();

        if (response.getStatusCode() == 304) {
            logger.debug("Config server responds with 304 HTTP status code.");
            return namespaceConfigCache.get();
        }

        NamespaceConfig namespaceConfig = response.getBody();
        logger.debug("Loaded config for {}: {}", namespaceName, namespaceConfig);
        return namespaceConfig;
    }

    public String assembleQueryConfigUrl(String namespace) {

        String configServerUrl = m_Client_config.getConfigServerUrlWithSlash();
        String pathExpanded = assemblePathExpanded(namespace);

        return configServerUrl + pathExpanded;
    }

    private String assemblePathExpanded(String namespace) {
        String path = "configs/%s/%s/%s/%s";
        List<String> pathParams = Lists.newArrayList(pathEscaper.escape(m_Client_config.getAppId()), pathEscaper.escape(m_Client_config.getApolloEnv().name().toLowerCase()), pathEscaper.escape(m_Client_config.getCluster()), pathEscaper.escape(namespace));
        Map<String, String> queryParams = Maps.newHashMap();


        NamespaceConfig previousConfig = namespaceConfigCache.get();
        if (previousConfig != null) {
            queryParams.put("releaseKey", queryParamEscaper.escape(previousConfig.getReleaseKey()));
        }

        if (!Strings.isNullOrEmpty(m_Client_config.getDataCenter())) {
            queryParams.put("dataCenter", queryParamEscaper.escape(m_Client_config.getDataCenter()));
        }

        String localIp = m_Client_config.getLocalIp();
        if (!Strings.isNullOrEmpty(localIp)) {
            queryParams.put("ip", queryParamEscaper.escape(localIp));
        }


        LongNamespaceVersion remoteMessages = longNamespaceVersion.get();
        if (remoteMessages != null) {
            queryParams.put("messages", queryParamEscaper.escape(gson.toJson(remoteMessages)));
        }

        String pathExpanded = String.format(path, pathParams.toArray());

        if (!queryParams.isEmpty()) {
            pathExpanded += "?" + MAP_JOINER.join(queryParams);
        }
        return pathExpanded;
    }

    /**
     * 提交给 长连接版本控制
     */
    private void add2VersionControl() {
        versionMonitor.add2VersionMonitor(namespaceName, this);
    }

    /**
     * 拉取 config端的 版本变更通知， 然后再 拉取 最新的配置
     *
     * @param longNamespaceVersion
     */
    public void onReceiveNewVersion(LongNamespaceVersion longNamespaceVersion) {
        m_longPollServiceDto.set(m_Client_config.buildServiceDTO());
        this.longNamespaceVersion.set(longNamespaceVersion);

        m_executorService.submit(new Runnable() {
            @Override
            public void run() {
                m_configNeedForceRefresh.set(true);
                trySync();
            }
        });
    }

    private List<ServiceDTO> getConfigServices() {
        List<ServiceDTO> services = new ArrayList<>();
        ServiceDTO service = ServiceDTO.builder().instanceId(m_Client_config.getAppId()).homepageUrl(m_Client_config.getConfigServerUrl()).appName(m_Client_config.getAppId()).build();
        services.add(service);
        if (services.size() == 0) {
            throw new ApolloConfigException("No available config service");
        }

        return services;
    }
}
