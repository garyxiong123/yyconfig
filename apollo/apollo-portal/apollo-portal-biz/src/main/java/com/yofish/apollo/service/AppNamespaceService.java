package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.youyu.common.helper.YyRequestInfoHelper;
import common.exception.BadRequestException;
import framework.apollo.core.ConfigConsts;
import framework.apollo.core.enums.ConfigFileFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Service
public class AppNamespaceService {
    @Autowired
    private AppNamespaceRepository appNamespaceRepository;
    @Transactional
    public void createDefaultAppNamespace(Long appId) {
        if (!isAppNamespaceNameUnique(appId, ConfigConsts.NAMESPACE_APPLICATION)) {
            throw new BadRequestException(String.format("App already has application namespace. AppId = %s", appId));
        }

        AppNamespace appNs = new AppNamespace();
        appNs.setApp(App.builder().id(appId).build());
        appNs.setName(ConfigConsts.NAMESPACE_APPLICATION);
        appNs.setComment("default app namespace");
        appNs.setFormat(ConfigFileFormat.Properties.getValue());
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

}
