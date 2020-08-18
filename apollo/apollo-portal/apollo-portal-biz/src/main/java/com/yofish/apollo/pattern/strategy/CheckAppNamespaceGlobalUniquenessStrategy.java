package com.yofish.apollo.pattern.strategy;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.yofish.apollo.domain.AppNamespace;
import com.yofish.apollo.enums.AppNamespaceType;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/7/26 上午11:54
 */
@Component
public class CheckAppNamespaceGlobalUniquenessStrategy {
    private static final int PRIVATE_APP_NAMESPACE_NOTIFICATION_COUNT = 5;
    private static final Joiner APP_NAMESPACE_JOINER = Joiner.on(",").skipNulls();

    @Autowired
    private AppNamespaceRepository appNamespaceRepository;

    public void checkAppNamespaceGlobalUniqueness(AppNamespace appNamespace) {
        checkPublicAppNamespaceGlobalUniqueness(appNamespace);
        checkProtectAppNamespaceGlobalUniqueness(appNamespace);

        List<AppNamespace> privateAppNamespaces = findAllPrivateAppNamespaces(appNamespace.getName());

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

    public AppNamespace findPublicAppNamespace(String namespaceName) {
        return appNamespaceRepository.findByNameAndAppNamespaceType(namespaceName, AppNamespaceType.Public);
    }

    public AppNamespace findProtectAppNamespace(String namespaceName) {
        return appNamespaceRepository.findByNameAndAppNamespaceType(namespaceName, AppNamespaceType.Protect);
    }

    private List<AppNamespace> findAllPrivateAppNamespaces(String namespaceName) {
        List<AppNamespace> appNamespaceList = appNamespaceRepository.findAllByNameAndAppNamespaceType(namespaceName, AppNamespaceType.Private);
        return appNamespaceList;
    }
}
