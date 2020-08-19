package com.ctrip.framework.apollo.configservice.controller.listener;

import com.ctrip.framework.apollo.configservice.controller.RegistryCenter;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.pattern.listener.releasemessage.ReleaseMessageListener;
import framework.apollo.core.dto.NamespaceChangeNotification;
import framework.apollo.tracer.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 发布订阅的主要类： 完成 注册 注册中心
 * @Date: 2020/4/16 上午11:24
 */
@Slf4j
@Service
public class ReleaseMessageListener4Registry implements ReleaseMessageListener {


    @Autowired
    private RegistryCenter registryCenter;


    @Override
    public void onReceiveReleaseMessage(ReleaseMessage message, String channel) {
        String namespaceKey = message.getNamespaceKey();
        handleMessageLog(message, channel, namespaceKey);

        NamespaceChangeNotification configNotification = message.buildConfigNotification();

        registryCenter.onReceiveReleaseMessage(configNotification, namespaceKey);

    }


    private void handleMessageLog(ReleaseMessage message, String channel, String content) {
        log.info("message received - channel: {}, message: {}", channel, message);

        Tracer.logEvent("Apollo.LongPoll.Messages", content);
    }


}
