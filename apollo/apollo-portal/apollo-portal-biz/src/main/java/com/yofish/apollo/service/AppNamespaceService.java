package com.yofish.apollo.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.enums.NamespaceType;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.yofish.apollo.repository.AppRepository;
import com.youyu.common.helper.YyRequestInfoHelper;
import common.exception.BadRequestException;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.ConfigFileFormat;
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
@Service
public class AppNamespaceService {
    private static final int PRIVATE_APP_NAMESPACE_NOTIFICATION_COUNT = 5;
    private static final Joiner APP_NAMESPACE_JOINER = Joiner.on(",").skipNulls();

    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    @Autowired
    private AppRepository appRepository;
    @Autowired
    private NamespaceService namespaceService;

    /**
     * 公共的app ns,能被其它项目关联到的app ns
     */
    public List<AppNamespace> findPublicAppNamespaces() {
        return appNamespaceRepository.findByType(NamespaceType.Public);
    }

    public AppNamespace findPublicAppNamespace(String namespaceName) {
        List<AppNamespace> appNamespaces = appNamespaceRepository.findByNameAndType(namespaceName, NamespaceType.Public);

        if (CollectionUtils.isEmpty(appNamespaces)) {
            return null;
        }

        return appNamespaces.get(0);
    }

    private List<AppNamespace> findAllPrivateAppNamespaces(String namespaceName) {
        return appNamespaceRepository.findByNameAndType(namespaceName, NamespaceType.Public);
    }

    public AppNamespace findByAppIdAndName(Long appId, String namespaceName) {
        return appNamespaceRepository.findByAppIdAndName(appId, namespaceName);
    }

    public List<AppNamespace> findByAppId(Long appId) {
        return appNamespaceRepository.findByAppId(appId);
    }

    @Transactional
    public void createDefaultAppNamespace(Long appId) {
        if (!isAppNamespaceNameUnique(appId, ConfigConsts.NAMESPACE_APPLICATION)) {
            throw new BadRequestException(String.format("App already has application namespace. AppId = %s", appId));
        }

        AppNamespace appNs = new AppNamespace();
        appNs.setApp(App.builder().id(appId).build());
        appNs.setName(ConfigConsts.NAMESPACE_APPLICATION);
        appNs.setComment("default app namespace");
        appNs.setFormat(ConfigFileFormat.Properties);
        String userId = YyRequestInfoHelper.getCurrentUserRealName();
        appNs.setCreateAuthor(userId);
        appNs.setUpdateAuthor(userId);

        appNamespaceRepository.save(appNs);
    }


    public boolean isAppNamespaceNameUnique(Long appId, String namespaceName) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(namespaceName, "Namespace must not be null");
        return Objects.isNull(appNamespaceRepository.findByAppIdAndName(appId, namespaceName));
    }

    @Transactional
    public AppNamespace createAppNamespace(AppNamespace appNamespace, boolean appendNamespacePrefix) {
        Long appId = appNamespace.getApp().getId();

        //add app org id as prefix
        App app = this.appRepository.findById(appId).orElse(null);
        if (app == null) {
            throw new BadRequestException("App not exist. AppId = " + appId);
        }

        StringBuilder appNamespaceName = new StringBuilder();
        //add prefix postfix
        appNamespaceName
                .append(NamespaceType.Public.equals(appNamespace.getType()) && appendNamespacePrefix ? app.getDepartment().getCode() + "." : "")
                .append(appNamespace.getName())
                .append(appNamespace.getFormat() == ConfigFileFormat.Properties ? "" : "." + appNamespace.getFormat());
        appNamespace.setName(appNamespaceName.toString());

        if (appNamespace.getComment() == null) {
            appNamespace.setComment("");
        }

        // globally uniqueness check for public app namespace
        if (appNamespace.getType().equals(NamespaceType.Public)) {
            checkAppNamespaceGlobalUniqueness(appNamespace);
        } else {
            // check private app namespace
            if (appNamespaceRepository.findByAppIdAndName(appNamespace.getApp().getId(), appNamespace.getName()) != null) {
                throw new BadRequestException("Private AppNamespace " + appNamespace.getName() + " already exists!");
            }
            // should not have the same with public app namespace
            checkPublicAppNamespaceGlobalUniqueness(appNamespace);
        }

        AppNamespace createdAppNamespace = appNamespaceRepository.save(appNamespace);

        namespaceService.createNamespaceForAppNamespaceInAllCluster(appNamespace.getApp().getId(), appNamespace.getName());

        return createdAppNamespace;
    }

    private void checkAppNamespaceGlobalUniqueness(AppNamespace appNamespace) {
        checkPublicAppNamespaceGlobalUniqueness(appNamespace);

        List<AppNamespace> privateAppNamespaces = findAllPrivateAppNamespaces(appNamespace.getName());

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
            throw new BadRequestException("AppNamespace " + appNamespace.getName() + " already exists as public namespace in appId: " + publicAppNamespace.getApp().getId() + "!");
        }
    }

}
