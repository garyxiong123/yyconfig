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

import com.yofish.apollo.domain.*;
import com.yofish.apollo.api.dto.PublicProtectNamespaceDto;
import com.yofish.apollo.enums.AppNamespaceType;
import com.yofish.apollo.enums.NamespaceType;
import com.yofish.apollo.model.AppNamespaceModel;
import com.yofish.apollo.pattern.factory.AppNamespaceFactory;
import com.yofish.apollo.repository.*;
import com.youyu.common.utils.YyAssert;
import com.yofish.yyconfig.common.common.dto.NamespaceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Slf4j
@Service
public class AppNamespaceService {


    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;
    @Autowired
    private AppNamespaceFactory appNamespaceFactory;


    public List<AppNamespace> findAllPublicAppNamespace() {
        List<AppNamespace> appNamespaces = appNamespaceRepository.findAllByAppNamespaceType(AppNamespaceType.Public);
        return appNamespaces;
    }


    public List<AppNamespace> findAllProtectAppNamespaceByAuthorized(String appCode) {
        App app = appRepository.findByAppCode(appCode);
        YyAssert.paramCheck(ObjectUtils.isEmpty(app), "appCode not exists");
        List<AppNamespace> appNamespaces = appNamespaceRepository.findAllByAppNamespaceTypeAndAuthorizedAppContains(AppNamespaceType.Protect, app);
        return appNamespaces;
    }

    public PublicProtectNamespaceDto findAllPublicAndAuthorizedNamespace(String appCode) {
        PublicProtectNamespaceDto dto = new PublicProtectNamespaceDto();

        List<AppNamespace> allPublicAppNamespace = findAllPublicAppNamespace();
        List<AppNamespace> authorized = findAllProtectAppNamespaceByAuthorized(appCode);
        if (allPublicAppNamespace != null) {
            List<NamespaceDTO> publicNamespaceDtoList = allPublicAppNamespace.stream().map(this::appNamespacesToDto).collect(Collectors.toList());
            dto.setPublicNamespaces(publicNamespaceDtoList);
        }
        if (authorized != null) {
            List<NamespaceDTO> authorizedNamespaceList = authorized.stream().map(this::appNamespacesToDto).collect(Collectors.toList());
            dto.setProtectNamespaces(authorizedNamespaceList);
        }
        return dto;
    }

    private NamespaceDTO appNamespacesToDto(AppNamespace appNamespace) {
        return NamespaceDTO.builder()
                .id(appNamespace.getId())
                .appCode(appNamespace.getApp().getAppCode())
                .namespaceName(appNamespace.getName())
                .build();
    }

    public AppNamespace findAppNamespace(long namespaceId) {
        Optional<AppNamespace> appNamespace = appNamespaceRepository.findById(namespaceId);
        return appNamespace.orElse(null);
    }

    public AppNamespace findAppNamespace(String namespaceName) {
        AppNamespace appNamespace = appNamespaceRepository.findByName(namespaceName);
        return appNamespace;
    }


    public AppNamespace findByAppIdAndName(Long appId, String namespaceName) {
        return appNamespaceRepository.findByAppAndName(new App(appId), namespaceName);
    }

    public AppNamespace findByAppCodeAndName(String appCode, String namespaceName) {
        return appNamespaceRepository.findByAppAppCodeAndName(appCode, namespaceName);
    }

    public AppNamespace findProtectAppNamespaceByAppIdAndName(Long appId, String namespaceName) {
        return appNamespaceRepository.findByApp_IdAndNameAndAppNamespaceType(appId, namespaceName, AppNamespaceType.Protect);
    }

    public <T extends AppNamespace> AppNamespace updateAppNamespace(T appNamespace) {
        appNamespaceRepository.save(appNamespace);
        return appNamespace;
    }

    public List<AppNamespace> findByAppId(Long appId) {
        return appNamespaceRepository.findByAppId(appId);
    }

    /**
     * 创建默认命名空间（新建项目时候会创建）
     *
     * @param appId
     * @return
     */



    /**
     * 创建命名空间
     *
     * @param appNamespaceModel
     * @return
     */
    public AppNamespace createAppNamespace(AppNamespaceModel appNamespaceModel) {
        return appNamespaceFactory.createAppNamespace(appNamespaceModel);
    }

    /**
     * 授权App
     *
     * @param appNamespace4protect
     * @return
     */
    public AppNamespace authorizedApp(AppNamespace appNamespace4protect) {
        appNamespace4protect.isPublic();
        this.appNamespaceRepository.save(appNamespace4protect);
        return appNamespace4protect;
    }


}
