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
package com.yofish.apollo.listener;

import com.yofish.apollo.service.PortalConfig;
import common.dto.AppDTO;
import common.utils.BeanUtils;
import framework.apollo.core.enums.Env;
import framework.apollo.tracer.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppInfoChangedListener {
  private static final Logger logger = LoggerFactory.getLogger(AppInfoChangedListener.class);

//  @Autowired
//  private AdminServiceAPI.AppAPI appAPI;
  @Autowired
  private PortalConfig portalSettings;

  @EventListener
  public void onAppInfoChange(AppInfoChangedEvent event) {
//    AppDTO appDTO = org.apache.commons.beanutils.BeanUtils.copyProperties(AppDTO.class, event.getApp());
    AppDTO appDTO = null;
    String appId = appDTO.getAppId();

    List<Env> envs = portalSettings.getActiveEnvs();
    for (Env env : envs) {
      try {
//        appAPI.updateApp(env, appDTO);
      } catch (Throwable e) {
        logger.error("Update app's info failed. Env = {}, AppId = {}", env, appId, e);
        Tracer.logError(String.format("Update app's info failed. Env = %s, AppId = %s", env, appId), e);
      }
    }
  }
}
