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
package com.yofish.apollo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.yofish.apollo.enums.AppNamespaceType;
import com.yofish.apollo.model.AppNamespaceModel;
import com.yofish.apollo.pattern.strategy.CheckAppNamespaceGlobalUniquenessStrategy;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.AppNamespaceRepository;
import com.yofish.apollo.repository.AppRepository;
import com.yofish.gary.dao.entity.BaseEntity;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AppNamespace extends BaseEntity {

    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REMOVE})
    private App app;

    @Enumerated(EnumType.STRING)
    private ConfigFileFormat format;

    private String comment;

    @Enumerated(EnumType.STRING)
    private AppNamespaceType appNamespaceType;

    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<App> authorizedApp;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "namespaceTypeId")
    private OpenNamespaceType openNamespaceType;

    public AppNamespace(Long id) {
        super(id);
    }

    public AppNamespace(Long id, String name, App app, ConfigFileFormat format, String comment) {
        super(id);
        this.name = name;
        this.app = app;
        this.format = ObjectUtils.isEmpty(format) ? ConfigFileFormat.Properties : format;
        this.comment = comment;
    }

    public AppNamespace(AppNamespaceModel appNamespaceModel) {
        super(appNamespaceModel.getAppId());
        this.name = appNamespaceModel.getName();
        this.app = getBeanByClass(AppRepository.class).findById(appNamespaceModel.getAppId()).get();
        this.format = ObjectUtils.isEmpty(appNamespaceModel.getFormat()) ? ConfigFileFormat.Properties : appNamespaceModel.getFormat();
        this.comment = appNamespaceModel.getComment();
        appNamespaceType = appNamespaceModel.getAppNamespaceType();
        appNamespaceType.doBuildAppNamespace(this, appNamespaceModel);
    }

    public void buildDefaultAppNamespace(Long appId) {
        app = App.builder().id(appId).build();
        name = ConfigConsts.NAMESPACE_APPLICATION;
        comment = "default app appNamespace";
        format = ConfigFileFormat.Properties;
        appNamespaceType = AppNamespaceType.Private;
        if (!isAppNamespaceNameUnique(appId, ConfigConsts.NAMESPACE_APPLICATION)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("App already has application appNamespace. AppId = %s", appId));
        }
    }

    public boolean isPublicOrProtect() {
        return appNamespaceType.equals(AppNamespaceType.Protect) || appNamespaceType.equals(AppNamespaceType.Public);
    }

    public AppEnvClusterNamespace getNamespaceByEnv(String env, String cluster, String type) {

        return getBeanByClass(AppEnvClusterNamespaceRepository.class).findAppEnvClusterNamespace(this.getApp().getAppCode(), env, this.name, cluster, type);
    }


    public boolean isAppNamespaceNameUnique() {
        Objects.requireNonNull(this, "AppNamespace must not be null");
        Objects.requireNonNull(this.getApp(), "App must not be null");
        return isAppNamespaceNameUnique(this.getApp().getId(), this.getName());
    }

    public boolean isAppNamespaceNameUnique(Long appId, String namespaceName) {
        Objects.requireNonNull(appId, "AppId must not be null");
        Objects.requireNonNull(namespaceName, "Namespace must not be null");
        return Objects.isNull(getBeanByClass(AppNamespaceRepository.class).findByAppAndName(new App(appId), namespaceName));
    }

    public void checkAppNamespaceGlobalUniqueness() {

        getBeanByClass(CheckAppNamespaceGlobalUniquenessStrategy.class).checkAppNamespaceGlobalUniqueness(this);
    }

    public boolean isPublic() {
        return appNamespaceType.equals(AppNamespaceType.Public);
    }


    public boolean hasChange(LocalDateTime updateTime) {
        return !this.getUpdateTime().isEqual(updateTime);
    }
}
