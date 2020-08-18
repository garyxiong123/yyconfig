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
package biz.message;

import biz.AbstractUnitTest;
import com.yofish.apollo.component.constant.PermissionType;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.pattern.listener.releasemessage.ReleaseMessageSender4Database;
import com.yofish.apollo.repository.ReleaseMessageRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DatabaseMessageSenderTest extends AbstractUnitTest {
  private ReleaseMessageSender4Database messageSender;
  @Mock
  private ReleaseMessageRepository releaseMessageRepository;

  @Before
  public void setUp() throws Exception {
    messageSender = new ReleaseMessageSender4Database();
    ReflectionTestUtils.setField(messageSender, "releaseMessageRepository", releaseMessageRepository);
  }

  @Test
  public void testSendMessage() throws Exception {
    String someMessage = "some-message";
    long someId = 1;
    ReleaseMessage someReleaseMessage = mock(ReleaseMessage.class);
    when(someReleaseMessage.getId()).thenReturn(someId);
    when(releaseMessageRepository.save(Matchers.any(ReleaseMessage.class))).thenReturn(someReleaseMessage);

    ArgumentCaptor<ReleaseMessage> captor = ArgumentCaptor.forClass(ReleaseMessage.class);

    messageSender.sendMessage(someMessage, PermissionType.Topics.APOLLO_RELEASE_TOPIC);

    verify(releaseMessageRepository, times(1)).save(captor.capture());
    assertEquals(someMessage, captor.getValue().getMessage());
  }

  @Test
  public void testSendUnsupportedMessage() throws Exception {
    String someMessage = "some-message";
    String someUnsupportedTopic = "some-invalid-topic";

    messageSender.sendMessage(someMessage, someUnsupportedTopic);

    verify(releaseMessageRepository, never()).save(Matchers.any(ReleaseMessage.class));
  }

  @Test(expected = RuntimeException.class)
  public void testSendMessageFailed() throws Exception {
    String someMessage = "some-message";
    when(releaseMessageRepository.save(Matchers.any(ReleaseMessage.class))).thenThrow(new RuntimeException());

    messageSender.sendMessage(someMessage, PermissionType.Topics.APOLLO_RELEASE_TOPIC);
  }
}
