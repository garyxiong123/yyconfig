package com.ctrip.framework.apollo.configservice.domain;

import com.ctrip.framework.apollo.configservice.util.LongNamespaceNameUtil;
import com.ctrip.framework.apollo.configservice.util.NamespaceNormalizer;
import com.ctrip.framework.apollo.configservice.wrapper.ClientConnection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.service.ReleaseMessageService;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.*;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass4Context;
import static com.yofish.yyconfig.common.common.utils.YyStringUtils.notEqual;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: config端的 client
 * @Date: 2020/8/22 下午11:06
 */
@Data
public class ConfigClient4Version extends ConfigClient {
    private String clientNsVersionMapString;
    private static final Type notificationsTypeReference = new TypeToken<List<NamespaceVersion>>() {
    }.getType();
    private ClientConnection clientConnection;
    private Map<String, NamespaceVersion> normalizedNsVersionMap;
    private Set<String> namespaces4Client = new HashSet<>();
    private Multimap<String, String> clientWatchedKeysMap;
    private Set<String> clientWatchedKeys;
    private Map<String, Long> namespaceVersionMap = new HashMap<>();

    public ConfigClient4Version(String appId, String cluster, String env, String dataCenter, String clientIp, String clientNsVersionMapString) {
        super(appId, cluster, env, dataCenter, clientIp);
        this.clientNsVersionMapString = clientNsVersionMapString;
        clientConnection = new ClientConnection();
        buildNormalizedNsVersionMap();

        buildNsVersionIdMap();

        buildMapAndSet();

    }

    public void buildNormalizedNsVersionMap() {
        List<NamespaceVersion> clientNsVersions = getBeanByClass4Context(Gson.class).fromJson(clientNsVersionMapString, notificationsTypeReference);
        if (isEmpty(clientNsVersions)) {
//      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Invalid format of notifications: " + notificationsAsString);
        }
        normalizedNsVersionMap = getBeanByClass4Context(NamespaceNormalizer.class).normalizeNsVersions2Map(appId, clientNsVersions);

    }


    public Map<String, Long> buildNsVersionIdMap() {
        Map<String, Long> clientSideNotifications = Maps.newHashMap();
        for (Map.Entry<String, NamespaceVersion> namespaceVersionEntry : normalizedNsVersionMap.entrySet()) {
            String namespaceName = namespaceVersionEntry.getKey();
            NamespaceVersion clientNsVersion = namespaceVersionEntry.getValue();
            namespaces4Client.add(namespaceName);
            clientSideNotifications.put(namespaceName, clientNsVersion.getReleaseMessageId());
            if (notEqual(clientNsVersion.getNamespaceName(), namespaceName)) {
                String originalNamespaceName = clientNsVersion.getNamespaceName();
                clientConnection.updateNsNameMapping(originalNamespaceName, namespaceName);
            }
        }

        if (isEmpty(namespaces4Client)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Invalid format of notifications: " + clientNsVersionMapString);
        }

        namespaceVersionMap = clientSideNotifications;
        return clientSideNotifications;
    }


    public void buildMapAndSet() {
        clientWatchedKeysMap = getBeanByClass4Context(LongNamespaceNameUtil.class).assembleLongNamespaceNameMap(appId, clusterName, env, namespaces4Client, dataCenter);
        clientWatchedKeys = Sets.newHashSet(clientWatchedKeysMap.values());
    }

    /**
     * 计算得出 新的 命名空间版本
     */
    public List<NamespaceVersion> calcNewNsVersions() {
        //查询最新的发布 版本

        List<ReleaseMessage> latestReleaseMessages = getBeanByClass4Context(ReleaseMessageService.class).findLatestReleaseMessagesGroupByMessages(clientWatchedKeys);
        if (isEmpty(latestReleaseMessages)) {
            return null;
        }

        Map<String, Long> latestNotificationMap = Maps.newHashMap();
        latestReleaseMessages.forEach((releaseMessage) -> latestNotificationMap.put(releaseMessage.getNamespaceKey(), releaseMessage.getId()));

        List<NamespaceVersion> newNsVersions = Lists.newArrayList();
        for (String namespace4Client : namespaces4Client) {
            long clientSideId = namespaceVersionMap.get(namespace4Client);
            long latestId = ConfigConsts.NOTIFICATION_ID_PLACEHOLDER;
            Collection<String> namespaceWatchedKeys = clientWatchedKeysMap.get(namespace4Client);
            for (String namespaceWatchedKey : namespaceWatchedKeys) {
                long namespaceNotificationId = latestNotificationMap.getOrDefault(namespaceWatchedKey, ConfigConsts.NOTIFICATION_ID_PLACEHOLDER);
                if (namespaceNotificationId > latestId) {
                    latestId = namespaceNotificationId;
                }
            }
            if (latestId > clientSideId) {
                NamespaceVersion notification = new NamespaceVersion(namespace4Client, latestId);
                namespaceWatchedKeys.stream().filter(latestNotificationMap::containsKey).forEach(namespaceWatchedKey ->
                        notification.addMessage(namespaceWatchedKey, latestNotificationMap.get(namespaceWatchedKey)));
                newNsVersions.add(notification);
            }
        }
        return newNsVersions;
    }
}
