package com.yofish.yyconfig.client.domain;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yofish.yyconfig.client.component.util.ExceptionUtil;
import com.yofish.yyconfig.client.component.util.http.HttpRequest;
import com.yofish.yyconfig.client.component.util.http.HttpResponse;
import com.yofish.yyconfig.client.component.util.http.HttpUtil;
import com.yofish.yyconfig.client.lifecycle.preboot.inject.ApolloInjector;
import com.yofish.yyconfig.client.lifecycle.preboot.internals.ClientConfig;
import com.yofish.yyconfig.client.repository.RemoteConfigRepository;
import com.yofish.yyconfig.client.timer.VersionMonitor;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import com.yofish.yyconfig.common.framework.apollo.core.dto.ServiceDTO;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;
import com.yofish.yyconfig.common.framework.apollo.core.schedule.ExponentialSchedulePolicy;
import com.yofish.yyconfig.common.framework.apollo.core.schedule.SchedulePolicy;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/8/21 上午9:11
 */
@Data
@AllArgsConstructor
@Builder
public class Client {
    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR);
    private static final long INIT_NOTIFICATION_ID = ConfigConsts.NOTIFICATION_ID_PLACEHOLDER;
    private SchedulePolicy m_longPollFailSchedulePolicyInSecond;
    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on("&").withKeyValueSeparator("=");
    private static final Escaper queryParamEscaper = UrlEscapers.urlFormParameterEscaper();
    private static final Logger logger = LoggerFactory.getLogger(VersionMonitor.class);
    private static final int LONG_POLLING_READ_TIMEOUT = 90 * 1000;
    private final Map<String, LongNamespaceVersion> longNamespaceVersionMap;//namespaceName -> watchedKey -> notificationId
    private Type m_responseType;
    private final Multimap<String, RemoteConfigRepository> remoteConfigRepositoryMap;
    private final ConcurrentMap<String, Long> namespaceVersionMap;
    private HttpUtil m_httpUtil;
    private Gson gson;

    private String appId;
    private String cluster;
    private String env;
    private String dataCenter;
    private ClientConfig clientConfig;


    public Client() {
        m_longPollFailSchedulePolicyInSecond = new ExponentialSchedulePolicy(1, 120); //in second

        longNamespaceVersionMap = Maps.newConcurrentMap();
        m_responseType = new TypeToken<List<NamespaceVersion>>() {}.getType();
        remoteConfigRepositoryMap = Multimaps.synchronizedSetMultimap(HashMultimap.<String, RemoteConfigRepository>create());
        namespaceVersionMap = Maps.newConcurrentMap();

        clientConfig = ApolloInjector.getInstance(ClientConfig.class);

        m_httpUtil = ApolloInjector.getInstance(HttpUtil.class);
        gson = new Gson();
    }


    public void nsVersionCompareAndSyncConfig() {
        final Random random = new Random();

        String url = null;
        ServiceDTO lastServiceDto = null;
        try {

            lastServiceDto = clientConfig.buildServiceDTO();

            url = assembleLongPollRefreshUrl();

            logger.debug("Long polling from {}", url);
            HttpRequest request = new HttpRequest(url);
            request.setReadTimeout(LONG_POLLING_READ_TIMEOUT);

            /**
             * 获取 变更的版本
             */
            final HttpResponse<List<NamespaceVersion>> response = m_httpUtil.doGet(request, m_responseType);

            logger.debug("Long polling response: {}, url: {}", response.getStatusCode(), url);
            if (response.getStatusCode() == 200 && response.getBody() != null) {
                //如果有版本变更 ，
                List<NamespaceVersion> newNamespaceVersions = response.getBody();

                updateNamespaceVersionMap(newNamespaceVersions);
                updateLongNamespaceVersionMap(newNamespaceVersions);

                notify(lastServiceDto, newNamespaceVersions);
            }

            //try to load balance
            if (response.getStatusCode() == 304 && random.nextBoolean()) {
                lastServiceDto = null;
            }

            m_longPollFailSchedulePolicyInSecond.success();
        } catch (Throwable ex) {
            lastServiceDto = null;
            Tracer.logEvent("ApolloConfigException", ExceptionUtil.getDetailMessage(ex));
            long sleepTimeInSecond = m_longPollFailSchedulePolicyInSecond.fail();
            logger.warn(
                    "Long polling failed, will retry in {} seconds. appCode: {}, appEnvCluster: {}, namespaces: {}, long polling url: {}, reason: {}",
                    sleepTimeInSecond, appId, cluster, assembleNamespaces(), url, ExceptionUtil.getDetailMessage(ex));
            try {
                TimeUnit.SECONDS.sleep(sleepTimeInSecond);
            } catch (InterruptedException ie) {
                //ignore
            }
        } finally {

        }
    }


    private void notify(ServiceDTO lastServiceDto, List<NamespaceVersion> newNamespaceVersions) {
        if (newNamespaceVersions == null || newNamespaceVersions.isEmpty()) {
            return;
        }
        for (NamespaceVersion newNamespaceVersion : newNamespaceVersions) {
            String namespaceName = newNamespaceVersion.getNamespaceName();
            //create a new list to avoid ConcurrentModificationException
            List<RemoteConfigRepository> toBeNotified = Lists.newArrayList(remoteConfigRepositoryMap.get(namespaceName));

            LongNamespaceVersion longNamespaceVersion = longNamespaceVersionMap.get(namespaceName);
            LongNamespaceVersion longNsVersion4Remote = longNamespaceVersion == null ? null : longNamespaceVersion.clone();
            //since .properties are filtered out by default, so we need to check if there is any listener for it
            toBeNotified.addAll(remoteConfigRepositoryMap.get(String.format("%s.%s", namespaceName, ConfigFileFormat.Properties.getValue())));

            for (RemoteConfigRepository remoteConfigRepository : toBeNotified) {
                try {
                    remoteConfigRepository.onLongPollNotified(lastServiceDto, longNsVersion4Remote);
                } catch (Throwable ex) {
                    Tracer.logError(ex);
                }
            }
        }
    }

    /**
     * 更新本地 版本管理的缓存
     *
     * @param namespaceVersions
     */
    private void updateNamespaceVersionMap(List<NamespaceVersion> namespaceVersions) {
        for (NamespaceVersion namespaceChange : namespaceVersions) {
            if (Strings.isNullOrEmpty(namespaceChange.getNamespaceName())) {
                continue;
            }
            String namespaceName = namespaceChange.getNamespaceName();
            if (namespaceVersionMap.containsKey(namespaceName)) {
                namespaceVersionMap.put(namespaceName, namespaceChange.getReleaseMessageId());
            }
            //since .properties are filtered out by default, so we need to check if there is notification with .properties suffix
            String namespaceNameWithPropertiesSuffix = String.format("%s.%s", namespaceName, ConfigFileFormat.Properties.getValue());

            if (namespaceVersionMap.containsKey(namespaceNameWithPropertiesSuffix)) {
                namespaceVersionMap.put(namespaceNameWithPropertiesSuffix, namespaceChange.getReleaseMessageId());
            }
        }
    }

    private void updateLongNamespaceVersionMap(List<NamespaceVersion> namespaceVersions) {
        for (NamespaceVersion newNamespaceVersion : namespaceVersions) {
            if (Strings.isNullOrEmpty(newNamespaceVersion.getNamespaceName())) {
                continue;
            }

            if (newNamespaceVersion.getLongNamespaceVersion() == null || newNamespaceVersion.getLongNamespaceVersion().isEmpty()) {
                continue;
            }

            LongNamespaceVersion localRemoteMessages = longNamespaceVersionMap.get(newNamespaceVersion.getNamespaceName());

            if (localRemoteMessages == null) {
                localRemoteMessages = new LongNamespaceVersion();
                longNamespaceVersionMap.put(newNamespaceVersion.getNamespaceName(), localRemoteMessages);
            }

            localRemoteMessages.mergeFrom(newNamespaceVersion.getLongNamespaceVersion());
        }
    }


    String assembleLongPollRefreshUrl() {
        Map<String, String> queryParams = Maps.newHashMap();
        queryParams.put("appId", queryParamEscaper.escape(appId));
        queryParams.put("cluster", queryParamEscaper.escape(cluster));
        queryParams.put("env", queryParamEscaper.escape(env));
        queryParams.put("notifications", queryParamEscaper.escape(assembleNsVersionsMapString()));

        if (!Strings.isNullOrEmpty(dataCenter)) {
            queryParams.put("dataCenter", queryParamEscaper.escape(dataCenter));
        }
        String localIp = clientConfig.getLocalIp();
        if (!Strings.isNullOrEmpty(localIp)) {
            queryParams.put("ip", queryParamEscaper.escape(localIp));
        }

        String params = MAP_JOINER.join(queryParams);
        String uri = clientConfig.getConfigServerUrl();
        if (!clientConfig.getConfigServerUrl().endsWith("/")) {
            uri += "/";
        }

        return uri + "notifications/v2?" + params;
    }


    private String assembleNamespaces() {
        return STRING_JOINER.join(remoteConfigRepositoryMap.keySet());
    }


    String assembleNsVersionsMapString() {
        List<NamespaceVersion> namespaceVersions = Lists.newArrayList();
        for (Map.Entry<String, Long> entry : namespaceVersionMap.entrySet()) {
            NamespaceVersion nsVersion = new NamespaceVersion(entry.getKey(), entry.getValue());
            namespaceVersions.add(nsVersion);
        }
        return gson.toJson(namespaceVersions);
    }


    public boolean addConfigRepository(String namespace, RemoteConfigRepository remoteConfigRepository) {
        boolean added = remoteConfigRepositoryMap.put(namespace, remoteConfigRepository);
        namespaceVersionMap.putIfAbsent(namespace, INIT_NOTIFICATION_ID);
        return added;
    }
}
