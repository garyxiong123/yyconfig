package com.ctrip.framework.apollo.configservice.domain;

import com.ctrip.framework.apollo.configservice.component.ConfigClient;
import com.ctrip.framework.apollo.configservice.component.util.LongNamespaceNameUtil;
import com.ctrip.framework.apollo.configservice.component.util.NamespaceNormalizer;
import com.ctrip.framework.apollo.configservice.component.wrapper.ClientConnection;
import com.ctrip.framework.apollo.configservice.pattern.strategy.VersionCompareStrategy;
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
    private String clientNsVersionMapStr;
    private static final Type notificationsTypeReference = new TypeToken<List<NamespaceVersion>>() {
    }.getType();
    private ClientConnection clientConnection;
    private Map<String, NamespaceVersion> normalized_Ns_NsVersion;
    private Set<String> namespaceSet = new HashSet<>();
    private Multimap<String, String> namespace_LongNs;   //namespace, 和 全命名空间 LongNs
    private Set<String> longNsNames;
    private Map<String, Long> ns2ReleaseMsgIdMap = new HashMap<>();

    private List<NamespaceVersion> clientNsVersions;

    private Map<String, Long> latest_LongNs_ReleaseMsgId;//最新的ns版本

    public ConfigClient4Version(String appId, String cluster, String env, String dataCenter, String clientIp, String clientNsVersionMapStr) {
        super(appId, cluster, env, dataCenter, clientIp);
        this.clientNsVersionMapStr = clientNsVersionMapStr;
        clientConnection = new ClientConnection();
        buildNormalizedNsVersionMap();

        buildNsVersionIdMap();

        buildLongNsNamesSet();

        latest_LongNs_ReleaseMsgId = getLastLNs2ReleaseMsgIdMap();

    }

    public void buildNormalizedNsVersionMap() {
        clientNsVersions = getBeanByClass4Context(Gson.class).fromJson(clientNsVersionMapStr, notificationsTypeReference);
        if (isEmpty(clientNsVersions)) {
//      throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Invalid format of notifications: " + notificationsAsString);
        }
        //TODO fix wangsongjun 加上 clientNsVersions = null 的场景，默认返回最新的版本数据
        normalized_Ns_NsVersion = getBeanByClass4Context(NamespaceNormalizer.class).normalizeNsVersions2Map(appId, clientNsVersions);

    }


    public Map<String, Long> buildNsVersionIdMap() {
        for (Map.Entry<String, NamespaceVersion> namespaceVersionEntry : normalized_Ns_NsVersion.entrySet()) {
            String namespaceName = namespaceVersionEntry.getKey();
            NamespaceVersion clientNsVersion = namespaceVersionEntry.getValue();
            namespaceSet.add(namespaceName);
            ns2ReleaseMsgIdMap.put(namespaceName, clientNsVersion.getReleaseMessageId());
            if (notEqual(clientNsVersion.getNamespaceName(), namespaceName)) {
                String originalNamespaceName = clientNsVersion.getNamespaceName();
                clientConnection.updateNsNameMapping(originalNamespaceName, namespaceName);
            }
        }

        if (isEmpty(namespaceSet)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "Invalid format of notifications: " + clientNsVersionMapStr);
        }

        return ns2ReleaseMsgIdMap;
    }


    public void buildLongNsNamesSet() {
        namespace_LongNs = getBeanByClass4Context(LongNamespaceNameUtil.class).assembleNamespace2LongNsMap(appId, clusterName, env, namespaceSet, dataCenter);
        longNsNames = Sets.newHashSet(namespace_LongNs.values());
    }

    /**
     * 计算得出 新的 命名空间版本
     */
    public List<NamespaceVersion> calcNewNsVersions() {
        //查询最新的发布 版本
        return getBeanByClass4Context(VersionCompareStrategy.class).calcNewNsVersions(this);
    }

    public String getLongNs(String namespace) {
        return namespace_LongNs.get(namespace).iterator().next();
    }


    /**
     * 这里存在两个纬度 ，一个是私有， 一个是 公共关联（两个Id相加）
     *
     * @param namespace
     * @return
     */
    public boolean isNewVersion(NamespaceVersion namespace) {

        //两组Id比价 每组两个Id

        long latestId = getLatestReleaseMsgId(namespace.getNamespaceName());

        return latestId > namespace.getReleaseMessageId();
    }


    public long getLatestReleaseMsgId(String namespace) {

        long latestId = ConfigConsts.NOTIFICATION_ID_PLACEHOLDER;

        Collection<String> longNsKeys = this.getNamespace_LongNs().get(namespace);
        for (String longNs : longNsKeys) {
            long releaseMessageId = latest_LongNs_ReleaseMsgId.getOrDefault(longNs, ConfigConsts.NOTIFICATION_ID_PLACEHOLDER);
            if (releaseMessageId > latestId) {
                latestId = releaseMessageId;
            }
        }
        return latestId;
    }


    private Map<String, Long> getLastLNs2ReleaseMsgIdMap() {
        List<ReleaseMessage> latestReleaseMessages = getBeanByClass4Context(ReleaseMessageService.class).findLatestReleaseMessagesGroupByLongNsNames(this.getLongNsNames());
        if (isEmpty(latestReleaseMessages)) {
            return null;
        }


        Map<String, Long> latestLongNs2ReleaseMsgIdMap = Maps.newHashMap();
        latestReleaseMessages.forEach(
                (releaseMessage) -> latestLongNs2ReleaseMsgIdMap.put(releaseMessage.getNamespaceKey(), releaseMessage.getId())
        );
        return latestLongNs2ReleaseMsgIdMap;
    }

}
