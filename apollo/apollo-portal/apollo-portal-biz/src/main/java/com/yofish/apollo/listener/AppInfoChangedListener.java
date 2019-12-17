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
