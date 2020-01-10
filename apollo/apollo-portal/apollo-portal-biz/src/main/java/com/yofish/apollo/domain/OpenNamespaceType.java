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
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * 公开namespace的类型
 *
 * @author WangSongJun
 * @date 2019-12-20
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OpenNamespaceType extends BaseEntity {

    @Column(unique = true)
    private String name;

    private String comment;

    @OneToMany(cascade = CascadeType.DETACH)
    @JoinColumn(name = "namespaceTypeId")
    private Set<AppNamespace> appNamespaces;

    public OpenNamespaceType(Long id) {
        super(id);
    }

    @Builder
    public OpenNamespaceType(Long id, String name, String comment, Set<AppNamespace> appNamespaces) {
        super(id);
        this.name = name;
        this.comment = comment;
        this.appNamespaces = appNamespaces;
    }
}
