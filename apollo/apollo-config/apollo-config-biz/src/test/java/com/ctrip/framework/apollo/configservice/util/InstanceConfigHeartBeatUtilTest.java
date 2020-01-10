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
package com.ctrip.framework.apollo.configservice.util;

import com.ctrip.framework.apollo.configservice.InstanceConfigRefreshModel;
import com.yofish.apollo.service.InstanceService;
import common.NamespaceBo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class InstanceConfigHeartBeatUtilTest {
  private InstanceConfigHeartBeatUtil instanceConfigAuditUtil;

  @Mock
  private InstanceService instanceService;
  private BlockingQueue<InstanceConfigRefreshModel> audits;

  private String someAppId;
  private String someConfigClusterName;
  private String someClusterName;
  private String someDataCenter;
  private String someIp;
  private String someConfigAppId;
  private String someConfigNamespace;
  private String someReleaseKey;
  private String someEnv;

  private InstanceConfigRefreshModel someAuditModel;
  private NamespaceBo namespaceBo;

  @Before
  public void setUp() throws Exception {
    instanceConfigAuditUtil = new InstanceConfigHeartBeatUtil();

    ReflectionTestUtils.setField(instanceConfigAuditUtil, "instanceService", instanceService);

    audits = (BlockingQueue<InstanceConfigRefreshModel>) ReflectionTestUtils.getField(instanceConfigAuditUtil, "audits");

    someAppId = "someAppId";
    someClusterName = "someClusterName";
    someDataCenter = "someDataCenter";
    someIp = "someIp";
    someConfigAppId = "someConfigAppId";
    someConfigClusterName = "someConfigClusterName";
    someConfigNamespace = "someConfigNamespace";
    someReleaseKey = "someReleaseKey";
    someEnv = "dev";
    namespaceBo = NamespaceBo.builder().build();
    someAuditModel = new InstanceConfigRefreshModel(namespaceBo,
        someConfigNamespace, someReleaseKey);
  }

  @Test
  public void testAudit() throws Exception {
    boolean result = instanceConfigAuditUtil.offerHeartBeat(namespaceBo, someIp, someReleaseKey);

    InstanceConfigRefreshModel audit = audits.poll();

    assertTrue(result);
    assertTrue(Objects.equals(someAuditModel, audit));
  }
/*
  @Test
  public void testDoAudit() throws Exception {
    long someInstanceId = 1;
    Instance someInstance = mock(Instance.class);

    when(someInstance.getId()).thenReturn(someInstanceId);
    when(instanceService.createInstance(any(Instance.class))).thenReturn(someInstance);

    instanceConfigAuditUtil.doHeartBeat(someAuditModel);

    verify(instanceService, times(1)).findInstance(someAppId, someClusterName, someDataCenter,
        someIp);
    verify(instanceService, times(1)).createInstance(any(Instance.class));
    verify(instanceService, times(1)).findInstanceConfig(someInstanceId, someConfigAppId,
        someConfigNamespace);
    verify(instanceService, times(1)).createInstanceConfig(any(InstanceConfig.class));
  }*/


}
