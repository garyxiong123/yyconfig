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
package apollo.internals;

import apollo.build.ApolloInjector;
import apollo.exceptions.ApolloConfigException;
import apollo.util.ConfigUtil;
import apollo.util.http.HttpRequest;
import apollo.util.http.HttpResponse;
import apollo.util.http.HttpUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.gson.reflect.TypeToken;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.ServiceNameConsts;
import framework.apollo.core.dto.ServiceDTO;
import framework.apollo.core.utils.ApolloThreadFactory;
import framework.apollo.tracer.Tracer;
import framework.foundation.Foundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ConfigServiceLocator {
    private static final Logger logger = LoggerFactory.getLogger(ConfigServiceLocator.class);
    private HttpUtil m_httpUtil;
    private ConfigUtil m_configUtil;
    private AtomicReference<List<ServiceDTO>> m_configServices;
    private Type m_responseType;
    private ScheduledExecutorService m_executorService;
    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");
    private static final Escaper queryParamEscaper = UrlEscapers.urlFormParameterEscaper();

    /**
     * Create a config service locator.
     */
    public ConfigServiceLocator() {
        List<ServiceDTO> initial = Lists.newArrayList();
        m_configServices = new AtomicReference<>(initial);
        m_responseType = new TypeToken<List<ServiceDTO>>() {
        }.getType();
        m_httpUtil = ApolloInjector.getInstance(HttpUtil.class);
        m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
        this.m_executorService = Executors.newScheduledThreadPool(1,
                ApolloThreadFactory.create("ConfigServiceLocator", true));
        initConfigServices();
    }

    private void initConfigServices() {
        // get from run time configurations 从配置中读取configService，  从分布式，改成单机配置，
        if (hasLocalConfig()) {
            buildConfigService4Local();
            return;
        }

        buildConfigService4Remote();
    }

    private void buildConfigService4Remote() {
        this.tryUpdateConfigServices();
        this.schedulePeriodicRefresh();
    }

    private boolean hasLocalConfig() {
        String appolo_meta = getApolloMeta();
        return appolo_meta != null;
    }

    private List<ServiceDTO> buildConfigService4Local() {
        String appolo_meta = getApolloMeta();
        logger.info("Located config services from apollo.configService configuration: {}, will not refresh config services from remote meta service!", appolo_meta);

        List<ServiceDTO> serviceDTOS = buildConfigServiceDTO(appolo_meta);
        setConfigServices(serviceDTOS);
        return serviceDTOS;
    }

    private List<ServiceDTO> buildConfigServiceDTO(String appolo_meta) {
        String[] configServiceUrls = appolo_meta.split(",");
        List<ServiceDTO> serviceDTOS = Lists.newArrayList();

        for (String configServiceUrl : configServiceUrls) {
            configServiceUrl = configServiceUrl.trim();
            ServiceDTO serviceDTO = new ServiceDTO();
            serviceDTO.setHomepageUrl(configServiceUrl);
            serviceDTO.setAppName(ServiceNameConsts.APOLLO_CONFIGSERVICE);
            serviceDTO.setInstanceId(configServiceUrl);
            serviceDTOS.add(serviceDTO);
        }
        return serviceDTOS;
    }

    private String getApolloMeta() {

        String configServices = System.getProperty(ConfigConsts.YYCONFIG_META_KEY);
//        configServices = "http://10.0.33.18:7243";
        if (isNullOrEmpty(configServices)) {
            // 2. Get from OS environment variable
            configServices = System.getenv(ConfigConsts.YYCONFIG_META_KEY);
        }
        if (isNullOrEmpty(configServices)) {
            // 3. Get from server.properties
            configServices = Foundation.server().getProperty(ConfigConsts.YYCONFIG_META_KEY, null);
        }
        return configServices;
    }

    /**
     * Get the config service info from remote meta server.
     *
     * @return the services dto
     */
    public List<ServiceDTO> getConfigServices() {
        if (m_configServices.get().isEmpty()) {
            updateConfigServices();
        }

        return m_configServices.get();
    }

    private boolean tryUpdateConfigServices() {
        try {
            updateConfigServices();
            return true;
        } catch (Throwable ex) {
            //ignore
        }
        return false;
    }

    private void schedulePeriodicRefresh() {
        this.m_executorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        logger.debug("refresh config services");
                        Tracer.logEvent("Apollo.MetaService", "periodicRefresh");
                        tryUpdateConfigServices();
                    }
                }, m_configUtil.getRefreshInterval(), m_configUtil.getRefreshInterval(),
                m_configUtil.getRefreshIntervalTimeUnit());
    }

    private synchronized void updateConfigServices() {
        String url = assembleMetaServiceUrl();

        HttpRequest request = new HttpRequest(url);
        int maxRetries = 2;
        Throwable exception = null;

        for (int i = 0; i < maxRetries; i++) {
            HttpResponse<List<ServiceDTO>> response = m_httpUtil.doGet(request, m_responseType);
            List<ServiceDTO> services = response.getBody();
            if (services == null || services.isEmpty()) {
                logConfigService("Empty response!");
                continue;
            }
            setConfigServices(services);
            return;

//        m_configUtil.getOnErrorRetryIntervalTimeUnit().sleep(m_configUtil.getOnErrorRetryInterval());
        }

        throw new ApolloConfigException(
                String.format("Get config services failed from %s", url), exception);
    }

    private void setConfigServices(List<ServiceDTO> services) {
        m_configServices.set(services);
        logConfigServices(services);
    }

    private String assembleMetaServiceUrl() {
        String domainName = m_configUtil.getMetaServerDomainName();
        String appId = m_configUtil.getAppId();
        String localIp = m_configUtil.getLocalIp();

        Map<String, String> queryParams = Maps.newHashMap();
        queryParams.put("appId", queryParamEscaper.escape(appId));
        if (!isNullOrEmpty(localIp)) {
            queryParams.put("ip", queryParamEscaper.escape(localIp));
        }

        return domainName + "/META-INF/services/config?" + MAP_JOINER.join(queryParams);
    }

    private void logConfigServices(List<ServiceDTO> serviceDtos) {
        for (ServiceDTO serviceDto : serviceDtos) {
            logConfigService(serviceDto.getHomepageUrl());
        }
    }

    private void logConfigService(String serviceUrl) {
        Tracer.logEvent("Apollo.Config.Services", serviceUrl);
    }
}
