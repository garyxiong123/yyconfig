package com.ctrip.framework.apollo.configservice.cache;

import com.ctrip.framework.apollo.configservice.component.ReleaseRepo;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.yofish.apollo.component.constant.PermissionType;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.service.ReleaseMessageService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.apollo.component.util.NamespaceKeyGenerator;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import com.yofish.yyconfig.common.framework.apollo.tracer.spi.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明: release 的缓存 纬度 为 namespacekey,
 * @Date: 2020/4/15 上午10:59
 */
@Component
public class ReleaseCache implements ReleaseRepo {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseCache.class);
    private static final long DEFAULT_EXPIRED_AFTER_ACCESS_IN_MINUTES = 60;//1 hour
    private static final String TRACER_EVENT_CACHE_INVALIDATE = "ConfigCache.Invalidate";
    private static final String TRACER_EVENT_CACHE_LOAD = "ConfigCache.LoadFromDB";
    private static final String TRACER_EVENT_CACHE_LOAD_ID = "ConfigCache.LoadFromDBById";
    private static final String TRACER_EVENT_CACHE_GET = "ConfigCache.Get";
    private static final String TRACER_EVENT_CACHE_GET_ID = "ConfigCache.GetById";
    public static final Splitter STRING_SPLITTER = Splitter.on(ConfigConsts.CLUSTER_NAMESPACE_SEPARATOR).omitEmptyStrings();

    @Autowired
    private ReleaseService releaseService;

    @Autowired
    private ReleaseMessageService releaseMessageService;

    private LoadingCache<String, ReleaseCache.ConfigCacheEntry> releaseCache; //namespaceKey

    private LoadingCache<Long, Optional<Release>> releaseMsgIdCache;  // releaseMessageId  :  Release

    private ReleaseCache.ConfigCacheEntry nullConfigCacheEntry;

    public ReleaseCache() {
        nullConfigCacheEntry = new ReleaseCache.ConfigCacheEntry(ConfigConsts.NOTIFICATION_ID_PLACEHOLDER, null);
    }

    @PostConstruct
    void initialize() {
        releaseCache = CacheBuilder.newBuilder()
                .expireAfterAccess(DEFAULT_EXPIRED_AFTER_ACCESS_IN_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<String, ReleaseCache.ConfigCacheEntry>() {
                    @Override
                    public ReleaseCache.ConfigCacheEntry load(String namespaceKey) throws Exception {
                        List<String> namespaceInfo = STRING_SPLITTER.splitToList(namespaceKey);
                        if (namespaceInfo.size() != 4) {
                            Tracer.logError(new IllegalArgumentException(String.format("Invalid cache load key %s", namespaceKey)));
                            return nullConfigCacheEntry;
                        }

                        Transaction transaction = Tracer.newTransaction(TRACER_EVENT_CACHE_LOAD, namespaceKey);
                        try {
                            ReleaseMessage latestReleaseMessage = releaseMessageService.findLatestReleaseMessageForMessages(Lists.newArrayList(namespaceKey));
                            Release latestRelease = releaseService.findLatestActiveRelease(namespaceInfo.get(0), namespaceInfo.get(1), namespaceInfo.get(2), namespaceInfo.get(3));

                            transaction.setStatus(Transaction.SUCCESS);

                            long releaseMessageId = latestReleaseMessage == null ? ConfigConsts.NOTIFICATION_ID_PLACEHOLDER : latestReleaseMessage.getId();

                            if (releaseMessageId == ConfigConsts.NOTIFICATION_ID_PLACEHOLDER && latestRelease == null) {
                                return nullConfigCacheEntry;
                            }

                            return new ReleaseCache.ConfigCacheEntry(releaseMessageId, latestRelease);
                        } catch (Throwable ex) {
                            transaction.setStatus(ex);
                            throw ex;
                        } finally {
                            transaction.complete();
                        }
                    }
                });
        releaseMsgIdCache = CacheBuilder.newBuilder()
                .expireAfterAccess(DEFAULT_EXPIRED_AFTER_ACCESS_IN_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<Long, Optional<Release>>() {
                    @Override
                    public Optional<Release> load(Long releaseId) throws Exception {
                        Transaction transaction = Tracer.newTransaction(TRACER_EVENT_CACHE_LOAD_ID, String.valueOf(releaseId));
                        try {
                            Release release = releaseService.findActiveOne(releaseId);

                            transaction.setStatus(Transaction.SUCCESS);

                            return Optional.ofNullable(release);
                        } catch (Throwable ex) {
                            transaction.setStatus(ex);
                            throw ex;
                        } finally {
                            transaction.complete();
                        }
                    }
                });
    }

    @Override
    public Release findActiveOne(long releaseId, LongNamespaceVersion clientMessages) {
        Tracer.logEvent(TRACER_EVENT_CACHE_GET_ID, String.valueOf(releaseId));
        return releaseMsgIdCache.getUnchecked(releaseId).orElse(null);
    }

    /**
     * 获取最新（可用:未过期的）的发布
     *
     * @return
     */
    @Override
    public Release findLatestActiveRelease(String appId, String env, String clusterName, String namespaceName,
                                           LongNamespaceVersion longNsVersion) {
        String namespacekey = NamespaceKeyGenerator.generate(appId, clusterName, env, namespaceName);

        Tracer.logEvent(TRACER_EVENT_CACHE_GET, namespacekey);

        ReleaseCache.ConfigCacheEntry cacheEntry = releaseCache.getUnchecked(namespacekey);


        if (cacheOutDated(longNsVersion, namespacekey, cacheEntry)) {  //cache is out-dated  缓存过期
            invalidate(namespacekey);//invalidate the cache and try to load from db again
            cacheEntry = releaseCache.getUnchecked(namespacekey);
        }

        return cacheEntry.getRelease();
    }

    private boolean cacheOutDated(LongNamespaceVersion longNsVersion, String namespacekey, ConfigCacheEntry cacheEntry) {
        return longNsVersion != null && longNsVersion.has(namespacekey) && longNsVersion.get(namespacekey) > cacheEntry.getNotificationId();
    }

    private void invalidate(String key) {
        releaseCache.invalidate(key);
        Tracer.logEvent(TRACER_EVENT_CACHE_INVALIDATE, key);
    }

    @Override
    public void onReceiveReleaseMessage(ReleaseMessage message, String channel) {
        logger.info("message received - channel: {}, message: {}", channel, message);
        if (!PermissionType.Topics.APOLLO_RELEASE_TOPIC.equals(channel) || Strings.isNullOrEmpty(message.getNamespaceKey())) {
            return;
        }

        try {
            loadRelease2Cache(message);
        } catch (Throwable ex) {
            //ignore
        }
    }

    private void loadRelease2Cache(ReleaseMessage releaseMessage) {
        invalidate(releaseMessage.getNamespaceKey());
        //warm up the cache
        releaseCache.getUnchecked(releaseMessage.getNamespaceKey());
    }


    private static class ConfigCacheEntry {
        private final long notificationId;
        private final Release release;

        public ConfigCacheEntry(long notificationId, Release release) {
            this.notificationId = notificationId;
            this.release = release;
        }

        public long getNotificationId() {
            return notificationId;
        }

        public Release getRelease() {
            return release;
        }
    }
}
