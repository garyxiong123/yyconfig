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
package com.yofish.apollo.service;

import com.google.common.collect.Lists;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.repository.ReleaseMessageRepository;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class ReleaseMessageService {
  @Autowired
  private ReleaseMessageRepository releaseMessageRepository;

  public ReleaseMessage findLatestReleaseMessageForMessages(Collection<String> messages) {
    if (CollectionUtils.isEmpty(messages)) {
      return null;
    }
    return releaseMessageRepository.findTopByNamespaceKeyInOrderByIdDesc(messages);
  }

  public List<ReleaseMessage> findLatestReleaseMessagesGroupByLongNsNames(Collection<String> longNsNames) {
    if (CollectionUtils.isEmpty(longNsNames)) {
      return Collections.emptyList();
    }
    List<Object[]> result = releaseMessageRepository.findLatestReleaseMessagesGroupByNamespaceKeys(longNsNames);
    List<ReleaseMessage> releaseMessages = Lists.newArrayList();
    for (Object[] o : result) {
      try {
        ReleaseMessage releaseMessage = new ReleaseMessage((String) o[0]);
        releaseMessage.setId((Long) o[1]);
        releaseMessages.add(releaseMessage);
      } catch (Exception ex) {
        Tracer.logError("Parsing LatestReleaseMessagesGroupByMessages failed", ex);
      }
    }
    return releaseMessages;
  }
}
