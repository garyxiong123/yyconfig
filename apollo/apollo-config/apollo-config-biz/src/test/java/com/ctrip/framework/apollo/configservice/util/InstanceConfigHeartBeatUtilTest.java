package com.ctrip.framework.apollo.configservice.util;

import com.ctrip.framework.apollo.configservice.InstanceConfigAuditModel;
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
  private BlockingQueue<InstanceConfigAuditModel> audits;

  private String someAppId;
  private String someConfigClusterName;
  private String someClusterName;
  private String someDataCenter;
  private String someIp;
  private String someConfigAppId;
  private String someConfigNamespace;
  private String someReleaseKey;
  private String someEnv;

  private InstanceConfigAuditModel someAuditModel;
  private NamespaceBo namespaceBo;

  @Before
  public void setUp() throws Exception {
    instanceConfigAuditUtil = new InstanceConfigHeartBeatUtil();

    ReflectionTestUtils.setField(instanceConfigAuditUtil, "instanceService", instanceService);

    audits = (BlockingQueue<InstanceConfigAuditModel>) ReflectionTestUtils.getField(instanceConfigAuditUtil, "audits");

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
    someAuditModel = new InstanceConfigAuditModel(namespaceBo,
        someConfigNamespace, someReleaseKey);
  }

  @Test
  public void testAudit() throws Exception {
    boolean result = instanceConfigAuditUtil.offerHeartBeat(namespaceBo, someIp, someReleaseKey);

    InstanceConfigAuditModel audit = audits.poll();

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
