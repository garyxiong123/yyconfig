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
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("Namespace4Protect")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AppNamespace4Protect extends AppNamespace {
    @Builder
    public AppNamespace4Protect(String name, App app, ConfigFileFormat format, String comment, Set<App> authorizedApp, OpenNamespaceType openNamespaceType) {
        super(name, app, format, comment);
        this.authorizedApp = authorizedApp;
        this.openNamespaceType = openNamespaceType;
    }

    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<App> authorizedApp;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "namespaceTypeId")
    private OpenNamespaceType openNamespaceType;
}
