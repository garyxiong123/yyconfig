package com.ctrip.framework.apollo.configservice.domain;

import com.ctrip.framework.apollo.configservice.cache.ReleaseMessageCache;
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
    private final ReleaseMessageCache releaseMessageCache;

    public ConfigClient4Version(String appId, String cluster, String env, String dataCenter, String clientIp, String clientNsVersionMapStr, ReleaseMessageCache releaseMessageCache) {
        super(appId, cluster, env, dataCenter, clientIp);
        this.clientNsVersionMapStr = clientNsVersionMapStr;
        this.releaseMessageCache = releaseMessageCache;

        clientConnection = new ClientConnection();

        buildNormalizedNsVersionMap();
        buildNsVersionIdMap();
        buildLongNsNamesSet();

        latest_LongNs_ReleaseMsgId = this.getLastLNs2ReleaseMsgIdMap(this.getLongNsNames());

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
     * 计算方法    每个nsversion 重的 map进行比对， 如果map中有变化，则 更新整个ns的map
     */
    public List<NamespaceVersion> calcNewNsVersions() {
        //查询最新的发布 版本
        return getBeanByClass4Context(VersionCompareStrategy.class).calcNewNsVersions(this);
    }


    /**
     * 这里存在两个纬度 ，一个是私有， 一个是 公共关联（两个Id相加）
     *
     * @param namespace
     * @return
     */
    public boolean isNewVersion(NamespaceVersion namespace) {
        return twoMapCompare(namespace);
    }

    /**
     * 新旧版本的map进行比价
     *
     * @param namespace
     * @return
     */
    private boolean twoMapCompare(NamespaceVersion namespace) {
        if (namespace.getLongNamespaceVersion() == null) {
            return true;
        }
        Map<String, Long> oldMap = namespace.getLongNamespaceVersion().getLongNsVersionMap();
        Map<String, Long> newMap = getNewMap(namespace.getNamespaceName());
        Iterator<Map.Entry<String, Long>> oldMapIter = oldMap.entrySet().iterator();
        while (oldMapIter.hasNext()) {
            Map.Entry<String, Long> oldEntry = oldMapIter.next();
            Long oldValue = oldEntry.getValue() == null ? -1L : oldEntry.getValue();
            Long newValue = newMap.get(oldEntry.getKey()) == null ? -1L : newMap.get(oldEntry.getKey());

            if (!oldValue.equals(newValue)) {//若两个map中相同key对应的value不相等
                return true;
            }
        }
        return false;
    }

    /**
     * 新的 versionMap 计算   通过  namespaceName =》 longNsKeys =》 id Map
     * @param namespaceName
     * @return
     */
    public Map<String, Long> getNewMap(String namespaceName) {
        Map<String, Long> newMap = new HashMap<>();
        Collection<String> longNsKeys = this.getNamespace_LongNs().get(namespaceName);
        for (String longNs : longNsKeys) {
            long releaseMessageId = latest_LongNs_ReleaseMsgId.getOrDefault(longNs, ConfigConsts.NOTIFICATION_ID_PLACEHOLDER);
            if (releaseMessageId > ConfigConsts.NOTIFICATION_ID_PLACEHOLDER) {
                newMap.put(longNs, releaseMessageId);
            }
        }
        return newMap;
    }


    /**
     * 取出 namespace 最新 也是 最大的releaseMessageId
     *  namespace 可能存在 ns+ clusterA 配置： 继承
     *
     * @param namespace
     * @return
     */
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

    public Map<String, Long> getLastLNs2ReleaseMsgIdMap(Set<String> longNsNames) {
        List<ReleaseMessage> latestReleaseMessages = releaseMessageCache.findLatestReleaseMessagesGroupByMessages(longNsNames);
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
