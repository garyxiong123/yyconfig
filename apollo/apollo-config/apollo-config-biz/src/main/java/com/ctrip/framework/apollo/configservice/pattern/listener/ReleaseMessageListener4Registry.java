package com.ctrip.framework.apollo.configservice.pattern.listener;

import com.ctrip.framework.apollo.configservice.cache.RegistryCenter;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.pattern.listener.releasemessage.ReleaseMessageListener;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 发布订阅的主要类： 完成 注册 注册中心
 * @Date: 2020/4/16 上午11:24
 */
@Slf4j
@Component
public class ReleaseMessageListener4Registry implements ReleaseMessageListener {


    @Autowired
    private RegistryCenter registryCenter;


    @Override
    public void onReceiveReleaseMessage(ReleaseMessage releaseMessage, String channel) {
        String longNsName = releaseMessage.getNamespaceKey();
        handleMessageLog(releaseMessage, channel, longNsName);

        NamespaceVersion nsVersion4Server = releaseMessage.buildNsVersion();

        registryCenter.publishNewNsVersion(nsVersion4Server, longNsName);

    }


    private void handleMessageLog(ReleaseMessage message, String channel, String content) {
        log.info("message received - channel: {}, message: {}", channel, message);

        Tracer.logEvent("Apollo.LongPoll.Messages", content);
    }


}
