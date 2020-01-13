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

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class ReleaseHistory extends BaseEntity {

    @ManyToOne(cascade = CascadeType.DETACH)
    private Release release;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Release previousRelease;

    @Column(name = "Operation")
    private int operation;

    @Column(name = "OperationContext", nullable = false)
    private String operationContext;

    private Long namespaceId;


}
