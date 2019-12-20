package com.yofish.apollo.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.repository.*;
import common.exception.BadRequestException;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Slf4j
@Service
public class AppNamespaceService {
    private static final int PRIVATE_APP_NAMESPACE_NOTIFICATION_COUNT = 5;
    private static final Joiner APP_NAMESPACE_JOINER = Joiner.on(",").skipNulls();

    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    @Autowired
    private AppNamespace4PublicRepository appNamespace4PublicRepository;
    @Autowired
    private AppNamespace4ProtectRepository appNamespace4ProtectRepository;
    @Autowired
    private AppNamespace4PrivateRepository appNamespace4PrivateRepository;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;


    public AppNamespace findPublicAppNamespace(String namespaceName) {
        List<AppNamespace4Public> appNamespaces = appNamespace4PublicRepository.findByName(namespaceName);

        if (CollectionUtils.isEmpty(appNamespaces)) {
            return null;
        }

        return appNamespaces.get(0);
    }

    private List<AppNamespace4Private> findAllPrivateAppNamespaces(String namespaceName) {
        List<AppNamespace4Private> appNamespaceList = appNamespace4PrivateRepository.findByName(namespaceName);
        return appNamespaceList;
    }

    public AppNamespace findByAppIdAndName(Long appId, String namespaceName) {
        return appNamespaceRepository.findByAppAndName(new App(appId), namespaceName);
    }

    public List<AppNamespace> findByAppId(Long appId) {
        return appNamespaceRepository.findByAppId(appId);
    }

    @Transactional
    public AppNamespace createDefaultAppNamespace(Long appId) {
        if (!isAppNamespaceNameUnique(appId, ConfigConsts.NAMESPACE_APPLICATION)) {
            throw new BadRequestException(String.format("App already has application appNamespace. AppId = %s", appId));
        }

        AppNamespace4Private appNs = AppNamespace4Private.builder()
                .app(App.builder().id(appId).build())
                .name(ConfigConsts.NAMESPACE_APPLICATION)
                .comment("default app appNamespace")
                .format(ConfigFileFormat.Properties)
                .build();

        appNs = appNamespace4PrivateRepository.save(appNs);
        return appNs;
    }


    public boolean isAppNamespaceNameUnique(Long appId, String namespaceName) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(namespaceName, "Namespace must not be null");
        return Objects.isNull(appNamespaceRepository.findByAppAndName(new App(appId), namespaceName));
    }

    public boolean isAppNamespaceNameUnique(AppNamespace appNamespace) {
        Objects.requireNonNull(appNamespace, "AppNamespace must not be null");
        Objects.requireNonNull(appNamespace.getApp(), "App must not be null");
        return isAppNamespaceNameUnique(appNamespace.getApp().getId(), appNamespace.getName());
    }

    public <T extends AppNamespace> T createAppNamespace(T appNamespace) {
        if (!isAppNamespaceNameUnique(appNamespace)) {
            throw new BadRequestException(String.format("App already has application appNamespace. AppId = %s", appNamespace.getApp().getId()));
        }

        appNamespaceRepository.save(appNamespace);

        appEnvClusterNamespaceService.createNamespaceForAppNamespaceInAllCluster(appNamespace);

        return appNamespace;
    }


    private void checkAppNamespaceGlobalUniqueness(AppNamespace appNamespace) {
        checkPublicAppNamespaceGlobalUniqueness(appNamespace);

        List<AppNamespace4Private> privateAppNamespaces = findAllPrivateAppNamespaces(appNamespace.getName());

        if (!CollectionUtils.isEmpty(privateAppNamespaces)) {
            Set<Long> appIds = Sets.newHashSet();
            for (AppNamespace ans : privateAppNamespaces) {
                appIds.add(ans.getApp().getId());
                if (appIds.size() == PRIVATE_APP_NAMESPACE_NOTIFICATION_COUNT) {
                    break;
                }
            }

            throw new BadRequestException(
                    "Public AppNamespace " + appNamespace.getName() + " already exists as private AppNamespace in appId: "
                            + APP_NAMESPACE_JOINER.join(appIds) + ", etc. Please select another name!");
        }
    }

    private void checkPublicAppNamespaceGlobalUniqueness(AppNamespace appNamespace) {
        AppNamespace publicAppNamespace = findPublicAppNamespace(appNamespace.getName());
        if (publicAppNamespace != null) {
            throw new BadRequestException("AppNamespace " + appNamespace.getName() + " already exists as public appNamespace in appId: " + publicAppNamespace.getApp().getId() + "!");
        }
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace4Branch(AppEnvClusterNamespace namespace) {
        return null;
    }


}
