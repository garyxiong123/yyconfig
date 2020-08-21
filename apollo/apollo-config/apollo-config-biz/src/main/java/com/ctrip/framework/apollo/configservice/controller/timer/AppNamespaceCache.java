package com.ctrip.framework.apollo.configservice.controller.timer;

import com.ctrip.framework.apollo.configservice.wrapper.CaseInsensitiveMapWrapper;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.yofish.yyconfig.common.common.utils.YyStringUtils;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 三级缓存 设计
 * client 并不知道 namespace 是 共有还是私有, 他应该
 * 1：client根据 AppCode+Namespace =》 namespace，有=》私有
 * 2：client根据         Namespace =》 namespace，有=》公有 和受保护的
 * @Date: 2020/7/28 上午9:31
 */
@Component
public class AppNamespaceCache {

    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    private static final Logger logger = LoggerFactory.getLogger(TimerTask4SyncAppNamespaceDB2Cache.class);


    private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).skipNulls();

    /**
     * store id -> AppNamespace 全部的
     */
    private Map<Long, AppNamespace> appNamespaceIdCache;

    /**
     * store namespaceName-> AppNamespace   public + protect
     **/
    private CaseInsensitiveMapWrapper<AppNamespace> appNamespaceCache4PublicProtect;

    /**
     * store appCode+namespaceName -> AppNamespace  私有设计
     **/
    private CaseInsensitiveMapWrapper<AppNamespace> appNamespaceCache4Private;


    public AppNamespaceCache() {
        appNamespaceCache4PublicProtect = new CaseInsensitiveMapWrapper<>(Maps.newConcurrentMap());
        appNamespaceCache4Private = new CaseInsensitiveMapWrapper<>(Maps.newConcurrentMap());
        appNamespaceIdCache = Maps.newConcurrentMap();
    }

    public AppNamespace findByAppIdAndNamespace(String appId, String namespaceName) {
        Preconditions.checkArgument(!YyStringUtils.isContainEmpty(appId, namespaceName), "appCode and namespaceName must not be empty");
        return appNamespaceCache4Private.get(STRING_JOINER.join(appId, namespaceName));
    }

    public List<AppNamespace> findByAppIdAndNamespaces(String appId, Set<String> namespaceNames) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appId), "appCode must not be null");
        if (namespaceNames == null || namespaceNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<AppNamespace> result = Lists.newArrayList();
        for (String namespaceName : namespaceNames) {
            AppNamespace appNamespace = appNamespaceCache4Private.get(STRING_JOINER.join(appId, namespaceName));
            if (appNamespace != null) {
                result.add(appNamespace);
            }
        }
        return result;
    }

    public AppNamespace findPublicNamespaceByName(String namespaceName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(namespaceName), "namespaceName must not be empty");
        return appNamespaceCache4PublicProtect.get(namespaceName);
    }

    public List<AppNamespace> findPublicNamespacesByNames(Set<String> namespaceNames) {
        if (namespaceNames == null || namespaceNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<AppNamespace> result = Lists.newArrayList();
        for (String namespaceName : namespaceNames) {
            AppNamespace appNamespace = appNamespaceCache4PublicProtect.get(namespaceName);
            if (appNamespace != null) {
                result.add(appNamespace);
            }
        }
        return result;
    }

    /**
     * 添加新增Appnamespace到缓存
     *
     * @param appNamespaces
     */
    public void addNewAppNamespacesToCache(List<AppNamespace> appNamespaces) {
        for (AppNamespace appNamespace : appNamespaces) {
            appNamespaceCache4Private.put(assembleAppNamespaceKey(appNamespace), appNamespace);
            appNamespaceIdCache.put(appNamespace.getId(), appNamespace);
            if (appNamespace.isPublic()) {
                appNamespaceCache4PublicProtect.put(appNamespace.getName(), appNamespace);
            }
        }
    }


    //for those updated or deleted app namespaces  == pull
    public void pollUpdateAndDelete2Cache() {
        List<Long> ids = Lists.newArrayList(appNamespaceIdCache.keySet());
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        List<List<Long>> partitionIds = Lists.partition(ids, 500);
        for (List<Long> toRebuild : partitionIds) {
            Iterable<AppNamespace> appNamespaces = appNamespaceRepository.findAllById(toRebuild);

            if (appNamespaces == null) {
                continue;
            }
            //handle updated
            Set<Long> foundIds = handleUpdatedAppNamespaces(appNamespaces);

            //handle deleted
            handleDeletedAppNamespaces(Sets.difference(Sets.newHashSet(toRebuild), foundIds));
        }
    }

    //for those updated app namespaces
    private Set<Long> handleUpdatedAppNamespaces(Iterable<AppNamespace> appNamespaces) {
        Set<Long> foundIds = Sets.newHashSet();
        for (AppNamespace appNamespace : appNamespaces) {
            foundIds.add(appNamespace.getId());
            AppNamespace thatInCache = appNamespaceIdCache.get(appNamespace.getId());
            if (appNamespace.hasChange(thatInCache.getUpdateTime())) {
                appNamespaceIdCache.put(appNamespace.getId(), appNamespace);

                String oldKey = assembleAppNamespaceKey(thatInCache);
                String newKey = assembleAppNamespaceKey(appNamespace);
                appNamespaceCache4Private.put(newKey, appNamespace);

                //in case appCode or namespaceName changes
                if (!newKey.equals(oldKey)) {
                    appNamespaceCache4Private.remove(oldKey);
                }

                if (appNamespace.isPublicOrProtect()) {
                    appNamespaceCache4PublicProtect.put(appNamespace.getName(), appNamespace);

                    //in case namespaceName changes
                    if (!appNamespace.getName().equals(thatInCache.getName()) && thatInCache.isPublicOrProtect()) {
                        appNamespaceCache4PublicProtect.remove(thatInCache.getName());
                    }
                } else if (thatInCache.isPublicOrProtect()) {
                    //just in case isPublic changes
                    appNamespaceCache4PublicProtect.remove(thatInCache.getName());
                }
                logger.info("Found AppNamespace changes, old: {}, new: {}", thatInCache, appNamespace);
            }
        }
        return foundIds;
    }

    //for those deleted app namespaces
    private void handleDeletedAppNamespaces(Set<Long> deletedIds) {
        if (CollectionUtils.isEmpty(deletedIds)) {
            return;
        }
        for (Long deletedId : deletedIds) {
            AppNamespace deleted = appNamespaceIdCache.remove(deletedId);
            if (deleted == null) {
                continue;
            }
            appNamespaceCache4Private.remove(assembleAppNamespaceKey(deleted));
            if (deleted.isPublic()) {
                AppNamespace publicAppNamespace = appNamespaceCache4PublicProtect.get(deleted.getName());
                // in case there is some dirty data, e.g. public appNamespace deleted in some app and now created in another app
                if (publicAppNamespace == deleted) {
                    appNamespaceCache4PublicProtect.remove(deleted.getName());
                }
            }
            logger.info("Found AppNamespace deleted, {}", deleted);
        }
    }


    private String assembleAppNamespaceKey(AppNamespace appNamespace) {
        return STRING_JOINER.join(appNamespace.getApp().getAppCode(), appNamespace.getName());
    }


    /**
     * 命名空间所属namespeace
     * @param appId
     * @param namespaces
     * @return
     */
    public Set<String> namespacesBelongToAppId(String appId, Set<String> namespaces) {
        if (ConfigConsts.NO_APPID_PLACEHOLDER.equalsIgnoreCase(appId)) {
            return Collections.emptySet();
        }
        List<AppNamespace> appNamespaces =
                this.findByAppIdAndNamespaces(appId, namespaces);

        if (appNamespaces == null || appNamespaces.isEmpty()) {
            return Collections.emptySet();
        }

        return FluentIterable.from(appNamespaces).transform(AppNamespace::getName).toSet();
    }


}
