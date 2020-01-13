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
import lombok.*;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:51
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Item extends BaseEntity {

    @ManyToOne(cascade = CascadeType.DETACH)
    private AppEnvClusterNamespace appEnvClusterNamespace;

    @Column(name = "keynumber", nullable = false)
    private String key;

    @Column(name = "value")
    @Lob
    private String value;

    @Column(name = "comment_desc")
    private String comment;

    private Integer lineNum;

    public Item(String key,String value,String comment,AppEnvClusterNamespace a,Integer lineNum) {
        this.key=key;
        this.value=value;
        this.comment=comment;
        this.appEnvClusterNamespace=a;
        this.lineNum=lineNum;
    }

}
