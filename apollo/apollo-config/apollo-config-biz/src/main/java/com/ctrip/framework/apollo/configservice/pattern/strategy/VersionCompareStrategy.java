package com.ctrip.framework.apollo.configservice.pattern.strategy;

import com.ctrip.framework.apollo.configservice.domain.ConfigClient4Version;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.service.ReleaseMessageService;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass4Context;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明:版本比价算法
 * @Date: 2020/8/27 上午11:23
 */
@Component
public class VersionCompareStrategy {


    public List<NamespaceVersion> calcNewNsVersions(ConfigClient4Version configClient4Version) {

        List<ReleaseMessage> latestReleaseMessages = getBeanByClass4Context(ReleaseMessageService.class).findLatestReleaseMessagesGroupByLongNsNames(configClient4Version.getLongNsNames());
        if (isEmpty(latestReleaseMessages)) {
            return null;
        }

        List<NamespaceVersion> newNsVersions = Lists.newArrayList();

        Map<String, Long> latestNotificationMap = Maps.newHashMap();
        latestReleaseMessages.forEach(
                (releaseMessage) -> latestNotificationMap.put(releaseMessage.getNamespaceKey(), releaseMessage.getId())
        );


        for (String namespace4Client : configClient4Version.getNamespaces4Client()) {
            long clientSideId = configClient4Version.getNamespaceVersionMap().get(namespace4Client);
            long latestId = ConfigConsts.NOTIFICATION_ID_PLACEHOLDER;
            Collection<String> namespaceWatchedKeys = configClient4Version.getClientWatchedKeysMap().get(namespace4Client);
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
