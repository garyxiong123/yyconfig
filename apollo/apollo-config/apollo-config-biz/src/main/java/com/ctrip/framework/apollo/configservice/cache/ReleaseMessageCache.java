package com.ctrip.framework.apollo.configservice.cache;

import com.ctrip.framework.apollo.configservice.controller.timer.sync.TimerTask4SyncReleaseMessage2Cache;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yofish.apollo.component.constant.PermissionType;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.pattern.listener.releasemessage.ReleaseMessageListener;
import com.yofish.apollo.repository.ReleaseMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明: 版本控制， 用于发现是否有新发布的变更
 * @Date: 2020/8/26 下午3:11
 */
@Component
public class ReleaseMessageCache implements ReleaseMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(TimerTask4SyncReleaseMessage2Cache.class);
    @Autowired
    private ReleaseMessageRepository releaseMessageRepository;
    @Autowired
    private TimerTask4SyncReleaseMessage2Cache timerTask4SyncReleaseMessage2Cache;
    private ConcurrentMap<String, ReleaseMessage> releaseMessageCache;   // key = app + cluster + env + application
    public volatile long maxIdScanned = 0;

    public ReleaseMessageCache() {
        releaseMessageCache = Maps.newConcurrentMap();
    }

    public ReleaseMessage findLatestReleaseMessageForMessages(Set<String> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }

        long maxReleaseMessageId = 0;
        ReleaseMessage result = null;
        for (String message : messages) {
            ReleaseMessage releaseMessage = releaseMessageCache.get(message);
            if (releaseMessage != null && releaseMessage.getId() > maxReleaseMessageId) {
                maxReleaseMessageId = releaseMessage.getId();
                result = releaseMessage;
            }
        }

        return result;
    }

    public List<ReleaseMessage> findLatestReleaseMessagesGroupByMessages(Set<String> messages) {
        if (CollectionUtils.isEmpty(messages)) {
            return Collections.emptyList();
        }
        List<ReleaseMessage> releaseMessages = Lists.newArrayList();

        for (String message : messages) {
            ReleaseMessage releaseMessage = releaseMessageCache.get(message);
            if (releaseMessage != null) {
                releaseMessages.add(releaseMessage);
            }
        }

        return releaseMessages;
    }

    private synchronized void mergeReleaseMessage2Cache(ReleaseMessage releaseMessage) {
        ReleaseMessage old = releaseMessageCache.get(releaseMessage.getNamespaceKey());
        if (old == null || releaseMessage.getId() > old.getId()) {
            releaseMessageCache.put(releaseMessage.getNamespaceKey(), releaseMessage);
            maxIdScanned = releaseMessage.getId();
        }
    }

    /**
     * 加载增量的发布消息
     */
    public void loadNewReleaseMessages() {
        boolean hasMore = true;
        while (hasMore && !Thread.currentThread().isInterrupted()) {
            //current batch is 500
            List<ReleaseMessage> releaseMessages = releaseMessageRepository.findFirst500ByIdGreaterThanOrderByIdAsc(maxIdScanned);
            if (CollectionUtils.isEmpty(releaseMessages)) {
                break;
            }
            releaseMessages.forEach(this::mergeReleaseMessage2Cache);
            int scanned = releaseMessages.size();
            maxIdScanned = releaseMessages.get(scanned - 1).getId();
            hasMore = scanned == 500;
            logger.info("Loaded {} release messages with startId {}", scanned, maxIdScanned);
        }
    }


    @Override
    public void onReceiveReleaseMessage(ReleaseMessage message, String channel) {
        //Could stop once the ReleaseMessageScanner starts to work
        timerTask4SyncReleaseMessage2Cache.stopScan();
        logger.info("message received - channel: {}, message: {}", channel, message);

        String namespaceKey = message.getNamespaceKey();

        if (!PermissionType.Topics.APOLLO_RELEASE_TOPIC.equals(channel) || Strings.isNullOrEmpty(namespaceKey)) {
            return;
        }

        long gap = message.getId() - maxIdScanned;
        if (gap == 1) {
            mergeReleaseMessage2Cache(message); //单独增加新增配置
        } else if (gap > 1) {
            //gap found!
            loadNewReleaseMessages(); // 批量拉取新的配置
        }
    }

}
