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
package com.yofish.apollo.openapi.util;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.domain.Release;
import com.yofish.platform.yyconfig.openapi.dto.*;
import com.yofish.yyconfig.common.common.dto.ClusterDTO;
import com.yofish.yyconfig.common.common.dto.ItemDTO;
import com.yofish.yyconfig.common.common.dto.ReleaseDTO;
import com.yofish.yyconfig.common.common.utils.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenApiBeanUtils {

  private static final Gson GSON = new Gson();
  private static final Type TYPE = new TypeToken<Map<String, String>>() {}.getType();

  public static OpenItemDTO transformFromItemDTO(Item item) {
    Preconditions.checkArgument(item != null);
    return BeanUtils.transform(OpenItemDTO.class, item);
  }

  public static ItemDTO transformToItemDTO(OpenItemDTO openItemDTO) {
    Preconditions.checkArgument(openItemDTO != null);
    return BeanUtils.transform(ItemDTO.class, openItemDTO);
  }

  public static OpenAppNamespaceDTO transformToOpenAppNamespaceDTO(AppNamespace appNamespace) {
    Preconditions.checkArgument(appNamespace != null);
    return BeanUtils.transform(OpenAppNamespaceDTO.class, appNamespace);
  }

  public static AppNamespace transformToAppNamespace(OpenAppNamespaceDTO openAppNamespaceDTO) {
    Preconditions.checkArgument(openAppNamespaceDTO != null);
    return BeanUtils.transform(AppNamespace.class, openAppNamespaceDTO);
  }

  public static OpenReleaseDTO transformFromReleaseDTO(Release release) {
    Preconditions.checkArgument(release != null);

    OpenReleaseDTO openReleaseDTO = BeanUtils.transform(OpenReleaseDTO.class, release);

    Map<String, String> configs = GSON.fromJson(release.getConfigurations(), TYPE);

    openReleaseDTO.setConfigurations(configs);
    return openReleaseDTO;
  }

  public static OpenReleaseDTO transformFromReleaseDTO(ReleaseDTO releaseDTO) {
    Preconditions.checkArgument(releaseDTO != null);

    OpenReleaseDTO openReleaseDTO = BeanUtils.transform(OpenReleaseDTO.class, releaseDTO);

    Map<String, String> configs = GSON.fromJson(releaseDTO.getConfigurations(), TYPE);

    openReleaseDTO.setConfigurations(configs);
    return openReleaseDTO;
  }

  public static List<OpenAppDTO> transformFromApps(final List<App> apps) {
    if (CollectionUtils.isEmpty(apps)) {
      return Collections.emptyList();
    }
    return apps.stream().map(OpenApiBeanUtils::transformFromApp).collect(Collectors.toList());
  }

  public static OpenAppDTO transformFromApp(final App app) {
    Preconditions.checkArgument(app != null);

    return BeanUtils.transform(OpenAppDTO.class, app);
  }

  public static OpenClusterDTO transformFromClusterDTO(ClusterDTO Cluster) {
    Preconditions.checkArgument(Cluster != null);
    return BeanUtils.transform(OpenClusterDTO.class, Cluster);
  }

  public static ClusterDTO transformToClusterDTO(OpenClusterDTO openClusterDTO) {
    Preconditions.checkArgument(openClusterDTO != null);
    return BeanUtils.transform(ClusterDTO.class, openClusterDTO);
  }
}
