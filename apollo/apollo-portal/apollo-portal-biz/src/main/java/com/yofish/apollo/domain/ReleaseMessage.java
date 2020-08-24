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

import com.yofish.apollo.component.util.NamespaceKeyGenerator;
import com.yofish.gary.dao.entity.BaseEntity;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 作用就是  通知 config端有新的发布
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class ReleaseMessage extends BaseEntity {

    /**
     * apollo-mini+default+dev+application = app + cluster + env + application
     */
    @Column(nullable = false)
    private String namespaceKey;

    public ReleaseMessage(AppEnvClusterNamespace namespace) {
        this.namespaceKey = namespace.generateNamespaceKey();
    }



    public NamespaceVersion buildConfigNotification() {

        String changedNamespace = NamespaceKeyGenerator.getNamespaceName(namespaceKey);
        NamespaceVersion configNotification = new NamespaceVersion(changedNamespace, this.getId());
        configNotification.addMessage(namespaceKey, this.getId());
        return configNotification;
    }
}

