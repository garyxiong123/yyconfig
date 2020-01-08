package com.yofish.apollo.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.PublicProtectNamespaceDto;
import com.yofish.apollo.repository.*;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyAssert;
import common.dto.NamespaceDTO;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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


    public List<AppNamespace4Public> findAllPublicAppNamespace() {
        List<AppNamespace4Public> appNamespaces = appNamespace4PublicRepository.findAll();
        return appNamespaces;
    }


    public List<AppNamespace4Protect> findAllProtectAppNamespaceByAuthorized(String appCode) {
        App app = appRepository.findByAppCode(appCode);
        YyAssert.paramCheck(ObjectUtils.isEmpty(app), "appCode not exists");
        List<AppNamespace4Protect> appNamespaces = appNamespace4ProtectRepository.findAllByAuthorizedAppContains(app);
        return appNamespaces;
    }

    public PublicProtectNamespaceDto findAllPublicAndAuthorizedNamespace(String appCode) {
        PublicProtectNamespaceDto dto = new PublicProtectNamespaceDto();

        List<AppNamespace4Public> allPublicAppNamespace = findAllPublicAppNamespace();
        List<AppNamespace4Protect> authorized = findAllProtectAppNamespaceByAuthorized(appCode);
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

    public AppNamespace findPublicAppNamespace(String namespaceName) {
        return appNamespace4PublicRepository.findByName(namespaceName);
    }

    public AppNamespace findProtectAppNamespace(String namespaceName) {
        return appNamespace4ProtectRepository.findByName(namespaceName);
    }

    private List<AppNamespace4Private> findAllPrivateAppNamespaces(String namespaceName) {
        List<AppNamespace4Private> appNamespaceList = appNamespace4PrivateRepository.findByName(namespaceName);
        return appNamespaceList;
    }

    public AppNamespace findByAppIdAndName(Long appId, String namespaceName) {
        return appNamespaceRepository.findByAppAndName(new App(appId), namespaceName);
    }

    public AppNamespace findByAppCodeAndName(String appCode, String namespaceName) {
        return appNamespaceRepository.findByAppAppCodeAndName(appCode, namespaceName);
    }

    public AppNamespace4Protect findProtectAppNamespaceByAppIdAndName(Long appId, String namespaceName) {
        return appNamespace4ProtectRepository.findByAppIdAndName(appId, namespaceName);
    }

    public <T extends AppNamespace> AppNamespace updateAppNamespace(T appNamespace) {
        appNamespaceRepository.save(appNamespace);
        return appNamespace;
    }

    public List<AppNamespace> findByAppId(Long appId) {
        return appNamespaceRepository.findByAppId(appId);
    }

    @Transactional
    public AppNamespace createDefaultAppNamespace(Long appId) {
        if (!isAppNamespaceNameUnique(appId, ConfigConsts.NAMESPACE_APPLICATION)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App already has application appNamespace. AppId = %s", appId));
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
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App already has application appNamespace. AppId = %s", appNamespace.getApp().getId()));
        }

        if (appNamespace instanceof AppNamespace4Public || appNamespace instanceof AppNamespace4Protect) {
            checkAppNamespaceGlobalUniqueness(appNamespace);
        }

        appNamespaceRepository.save(appNamespace);

        appEnvClusterNamespaceService.createNamespaceForAppNamespaceInAllCluster(appNamespace);

        return appNamespace;
    }

    public AppNamespace4Protect authorizedApp(AppNamespace4Protect protect) {
        this.appNamespace4ProtectRepository.save(protect);
        return protect;
    }

    private void checkAppNamespaceGlobalUniqueness(AppNamespace appNamespace) {
        checkPublicAppNamespaceGlobalUniqueness(appNamespace);
        checkProtectAppNamespaceGlobalUniqueness(appNamespace);

        List<AppNamespace4Private> privateAppNamespaces = findAllPrivateAppNamespaces(appNamespace.getName());

        if (!CollectionUtils.isEmpty(privateAppNamespaces)) {
            Set<Long> appIds = Sets.newHashSet();
            for (AppNamespace ans : privateAppNamespaces) {
                appIds.add(ans.getApp().getId());
                if (appIds.size() == PRIVATE_APP_NAMESPACE_NOTIFICATION_COUNT) {
                    break;
                }
            }

            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG,
                    "Public AppNamespace " + appNamespace.getName() + " already exists as private AppNamespace in appCode: "
                            + APP_NAMESPACE_JOINER.join(appIds) + ", etc. Please select another name!");
        }
    }

    private void checkPublicAppNamespaceGlobalUniqueness(AppNamespace appNamespace) {
        AppNamespace publicAppNamespace = findPublicAppNamespace(appNamespace.getName());
        if (publicAppNamespace != null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "AppNamespace " + appNamespace.getName() + " already exists as public appNamespace in appCode: " + publicAppNamespace.getApp().getAppCode() + "!");
        }
    }

    private void checkProtectAppNamespaceGlobalUniqueness(AppNamespace appNamespace) {
        AppNamespace publicAppNamespace = findProtectAppNamespace(appNamespace.getName());
        if (publicAppNamespace != null) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "AppNamespace " + appNamespace.getName() + " already exists as protect appNamespace in appCode: " + publicAppNamespace.getApp().getAppCode() + "!");
        }
    }

    public AppEnvClusterNamespace findAppEnvClusterNamespace4Branch(AppEnvClusterNamespace namespace) {
        return null;
    }


}
