/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.apollo.pattern.listener.releasemessage;

import com.google.common.collect.Queues;
import com.yofish.apollo.component.constant.PermissionType;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.repository.ReleaseMessageRepository;
import com.yofish.yyconfig.common.framework.apollo.core.utils.ApolloThreadFactory;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ReleaseMessageSender4Database implements ReleaseMessageSender {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseMessageSender4Database.class);
    private static final int CLEAN_QUEUE_MAX_SIZE = 100;
    private BlockingQueue<Long> toClean = Queues.newLinkedBlockingQueue(CLEAN_QUEUE_MAX_SIZE);
    private final ExecutorService cleanExecutorService;
    private final AtomicBoolean cleanStopped;

    @Autowired
    private ReleaseMessageRepository releaseMessageRepository;

    public ReleaseMessageSender4Database() {
        cleanExecutorService = Executors.newSingleThreadExecutor(ApolloThreadFactory.create("DatabaseMessageSender", true));
        cleanStopped = new AtomicBoolean(false);
    }

    /**
     * 发送 release 消息的同时， 清除本地的DB（队列）消息
     *
     * @param message
     * @param channel
     */
    @Override
    @Transactional
    public void sendMessage(String message, String channel) {
        logger.info("Sending message {} to channel {}", message, channel);

        if (!Objects.equals(channel, PermissionType.Topics.APOLLO_RELEASE_TOPIC)) {
            logger.warn("Channel {} not supported by DatabaseMessageSender!");
            return;
        }

        ReleaseMessage newMessage = releaseMessageRepository.save(new ReleaseMessage(message));
        toClean.offer(newMessage.getId());
    }

    @PostConstruct
    private void initialize() {
        cleanExecutorService.submit(() -> {
            while (!cleanStopped.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    Long rm = toClean.poll(1, TimeUnit.SECONDS);
                    if (rm != null) {
                        cleanMessage(rm);
                    } else {
                        TimeUnit.SECONDS.sleep(500);
                    }
                } catch (Throwable ex) {
                    Tracer.logError(ex);
                }
            }
        });
    }

    private void cleanMessage(Long id) {
        boolean hasMore = true;
        //double check in case the release message is rolled back
        ReleaseMessage releaseMessage = releaseMessageRepository.findById(id).get();
        if (releaseMessage == null) {
            return;
        }
        while (hasMore && !Thread.currentThread().isInterrupted()) {
            List<ReleaseMessage> messages = releaseMessageRepository.findFirst100ByNamespaceKeyAndIdLessThanOrderByIdAsc(
                    releaseMessage.getNamespaceKey(), releaseMessage.getId());

            releaseMessageRepository.deleteAll(messages);
            hasMore = messages.size() == 100;

            messages.forEach(toRemove -> Tracer.logEvent(
                    String.format("ReleaseMessage.Clean.%s", toRemove.getNamespaceKey()), String.valueOf(toRemove.getId())));
        }
    }

    void stopClean() {
        cleanStopped.set(true);
    }
}
