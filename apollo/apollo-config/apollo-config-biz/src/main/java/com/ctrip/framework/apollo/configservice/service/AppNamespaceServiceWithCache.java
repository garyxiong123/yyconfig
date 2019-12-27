package com.ctrip.framework.apollo.configservice.service;

import com.ctrip.framework.apollo.configservice.wrapper.CaseInsensitiveMapWrapper;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.AppNamespace4Public;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.yofish.apollo.service.PortalConfig;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.utils.ApolloThreadFactory;
import common.utils.YyStringUtils;
import framework.apollo.tracer.Tracer;
import framework.apollo.tracer.spi.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class AppNamespaceServiceWithCache implements InitializingBean {
  private static final Logger logger = LoggerFactory.getLogger(AppNamespaceServiceWithCache.class);
  private static final Joiner STRING_JOINER = Joiner.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR)
      .skipNulls();
  @Autowired
  private AppNamespaceRepository appNamespaceRepository;

  @Autowired
  private PortalConfig bizConfig;

  private int scanInterval;
  private TimeUnit scanIntervalTimeUnit;
  private int rebuildInterval =3;
  private TimeUnit rebuildIntervalTimeUnit = TimeUnit.SECONDS;
  private ScheduledExecutorService scheduledExecutorService;
  private long maxIdScanned;

  //store namespaceName -> AppNamespace
  private CaseInsensitiveMapWrapper<AppNamespace> publicAppNamespaceCache;

  //store appId+namespaceName -> AppNamespace
  private CaseInsensitiveMapWrapper<AppNamespace> appNamespaceCache;

  //store id -> AppNamespace
  private Map<Long, AppNamespace> appNamespaceIdCache;

  public AppNamespaceServiceWithCache() {
    initialize();
  }

  private void initialize() {
    maxIdScanned = 0;
    publicAppNamespaceCache = new CaseInsensitiveMapWrapper<>(Maps.newConcurrentMap());
    appNamespaceCache = new CaseInsensitiveMapWrapper<>(Maps.newConcurrentMap());
    appNamespaceIdCache = Maps.newConcurrentMap();
    scheduledExecutorService = Executors.newScheduledThreadPool(1, ApolloThreadFactory
        .create("AppNamespaceServiceWithCache", true));
  }

  public AppNamespace findByAppIdAndNamespace(String appId, String namespaceName) {
    Preconditions.checkArgument(!YyStringUtils.isContainEmpty(appId, namespaceName), "appId and namespaceName must not be empty");
    return appNamespaceCache.get(STRING_JOINER.join(appId, namespaceName));
  }

  public List<AppNamespace> findByAppIdAndNamespaces(String appId, Set<String> namespaceNames) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(appId), "appId must not be null");
    if (namespaceNames == null || namespaceNames.isEmpty()) {
      return Collections.emptyList();
    }
    List<AppNamespace> result = Lists.newArrayList();
    for (String namespaceName : namespaceNames) {
      AppNamespace appNamespace = appNamespaceCache.get(STRING_JOINER.join(appId, namespaceName));
      if (appNamespace != null) {
        result.add(appNamespace);
      }
    }
    return result;
  }

  public AppNamespace findPublicNamespaceByName(String namespaceName) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(namespaceName), "namespaceName must not be empty");
    return publicAppNamespaceCache.get(namespaceName);
  }

  public List<AppNamespace> findPublicNamespacesByNames(Set<String> namespaceNames) {
    if (namespaceNames == null || namespaceNames.isEmpty()) {
      return Collections.emptyList();
    }

    List<AppNamespace> result = Lists.newArrayList();
    for (String namespaceName : namespaceNames) {
      AppNamespace appNamespace = publicAppNamespaceCache.get(namespaceName);
      if (appNamespace != null) {
        result.add(appNamespace);
      }
    }
    return result;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    populateDataBaseInterval();
    scanNewAppNamespaces(); //block the startup process until load finished
    scanIntervalTimeUnit = TimeUnit.SECONDS;
    scanInterval = 3;

    scheduledExecutorService.scheduleAtFixedRate(() -> {
      Transaction transaction = Tracer.newTransaction("Apollo.AppNamespaceServiceWithCache",
          "rebuildCache");
      try {
        this.updateAndDeleteCache();
        transaction.setStatus(Transaction.SUCCESS);
      } catch (Throwable ex) {
        transaction.setStatus(ex);
        logger.error("Rebuild cache failed", ex);
      } finally {
        transaction.complete();
      }
    }, rebuildInterval, rebuildInterval, rebuildIntervalTimeUnit);
    scheduledExecutorService.scheduleWithFixedDelay(this::scanNewAppNamespaces, scanInterval,
        scanInterval, scanIntervalTimeUnit);
  }

  private void scanNewAppNamespaces() {
    Transaction transaction = Tracer.newTransaction("Apollo.AppNamespaceServiceWithCache",
        "scanNewAppNamespaces");
    try {
      this.loadNewAppNamespaces();
      transaction.setStatus(Transaction.SUCCESS);
    } catch (Throwable ex) {
      transaction.setStatus(ex);
      logger.error("Load new app namespaces failed", ex);
    } finally {
      transaction.complete();
    }
  }

  //for those new app namespaces
  private void loadNewAppNamespaces() {
    boolean hasMore = true;
    while (hasMore && !Thread.currentThread().isInterrupted()) {
      //current batch is 500
      List<AppNamespace> appNamespaces = appNamespaceRepository.findFirst500ByIdGreaterThanOrderByIdAsc(maxIdScanned);
      if (CollectionUtils.isEmpty(appNamespaces)) {
        break;
      }
      mergeAppNamespaces(appNamespaces);
      int scanned = appNamespaces.size();
      maxIdScanned = appNamespaces.get(scanned - 1).getId();
      hasMore = scanned == 500;
      logger.info("Loaded {} new app namespaces with startId {}", scanned, maxIdScanned);
    }
  }

  private void mergeAppNamespaces(List<AppNamespace> appNamespaces) {
    for (AppNamespace appNamespace : appNamespaces) {
      appNamespaceCache.put(assembleAppNamespaceKey(appNamespace), appNamespace);
      appNamespaceIdCache.put(appNamespace.getId(), appNamespace);
      if (appNamespace instanceof AppNamespace4Public) {
        publicAppNamespaceCache.put(appNamespace.getName(), appNamespace);
      }
    }
  }

  //for those updated or deleted app namespaces
  private void updateAndDeleteCache() {
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
      if (thatInCache != null ) {
        appNamespaceIdCache.put(appNamespace.getId(), appNamespace);
        String oldKey = assembleAppNamespaceKey(thatInCache);
        String newKey = assembleAppNamespaceKey(appNamespace);
        appNamespaceCache.put(newKey, appNamespace);

        //in case appId or namespaceName changes
        if (!newKey.equals(oldKey)) {
          appNamespaceCache.remove(oldKey);
        }

        if (appNamespace instanceof AppNamespace4Public) {
          publicAppNamespaceCache.put(appNamespace.getName(), appNamespace);

          //in case namespaceName changes
          if (!appNamespace.getName().equals(thatInCache.getName()) && thatInCache instanceof AppNamespace4Public) {
            publicAppNamespaceCache.remove(thatInCache.getName());
          }
        } else if (thatInCache instanceof AppNamespace4Public) {
          //just in case isPublic changes
          publicAppNamespaceCache.remove(thatInCache.getName());
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
      appNamespaceCache.remove(assembleAppNamespaceKey(deleted));
      if (deleted instanceof AppNamespace4Public) {
        AppNamespace publicAppNamespace = publicAppNamespaceCache.get(deleted.getName());
        // in case there is some dirty data, e.g. public appNamespace deleted in some app and now created in another app
        if (publicAppNamespace == deleted) {
          publicAppNamespaceCache.remove(deleted.getName());
        }
      }
      logger.info("Found AppNamespace deleted, {}", deleted);
    }
  }

  private String assembleAppNamespaceKey(AppNamespace appNamespace) {
    return STRING_JOINER.join(appNamespace.getApp().getAppCode(), appNamespace.getName());
  }

  private void populateDataBaseInterval() {
//    scanInterval = bizConfig.appNamespaceCacheScanInterval();
//    scanIntervalTimeUnit = bizConfig.appNamespaceCacheScanIntervalTimeUnit();
//    rebuildInterval = bizConfig.appNamespaceCacheRebuildInterval();
//    rebuildIntervalTimeUnit = bizConfig.appNamespaceCacheRebuildIntervalTimeUnit();
  }

  //only for test use
  private void reset() throws Exception {
    scheduledExecutorService.shutdownNow();
    initialize();
    afterPropertiesSet();
  }
}
