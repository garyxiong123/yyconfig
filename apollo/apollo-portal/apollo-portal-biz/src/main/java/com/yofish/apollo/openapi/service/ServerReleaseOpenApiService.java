/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.yofish.apollo.openapi.service;

import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.openapi.util.OpenApiBeanUtils;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.platform.yyconfig.openapi.api.ReleaseOpenApiService;
import com.yofish.platform.yyconfig.openapi.dto.NamespaceReleaseDTO;
import com.yofish.platform.yyconfig.openapi.dto.OpenReleaseDTO;
import com.yofish.yyconfig.common.common.dto.ReleaseDTO;
import com.yofish.yyconfig.common.framework.apollo.core.enums.Env;
import org.springframework.stereotype.Service;

/**
 * @author wxq
 */
@Service
public class ServerReleaseOpenApiService implements ReleaseOpenApiService {
  private final ReleaseService releaseService;
  private final AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;

  public ServerReleaseOpenApiService(
          ReleaseService releaseService, AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository) {
    this.releaseService = releaseService;
      this.appEnvClusterNamespaceRepository = appEnvClusterNamespaceRepository;
  }

  @Override
  public OpenReleaseDTO publishNamespace(String appId, String env, String clusterName,
                                         String namespaceName, NamespaceReleaseDTO releaseDTO) {
    AppEnvClusterNamespace appEnvClusterNamespace = appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appId, Env.valueOf(env).toString(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue());

      Release publish = releaseService.publish(appEnvClusterNamespace, releaseDTO.getReleaseTitle(), releaseDTO.getReleaseComment(), releaseDTO.getReleasedBy(), releaseDTO.isEmergencyPublish());


      return OpenApiBeanUtils.transformFromReleaseDTO(publish);
  }

  @Override
  public OpenReleaseDTO getLatestActiveRelease(String appId, String env, String clusterName,
      String namespaceName) {
      AppEnvClusterNamespace appEnvClusterNamespace = appEnvClusterNamespaceRepository.findAppEnvClusterNamespace(appId, Env.valueOf(env).toString(), namespaceName, clusterName, AppEnvClusterNamespace.Type.Main.getValue());

      ReleaseDTO releaseDTO = releaseService.loadLatestRelease(appEnvClusterNamespace);
    if (releaseDTO == null) {
      return null;
    }

    return OpenApiBeanUtils.transformFromReleaseDTO(releaseDTO);
  }

}
