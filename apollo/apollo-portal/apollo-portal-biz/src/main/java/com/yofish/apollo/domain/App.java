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

import com.yofish.apollo.repository.AppRepository;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class App extends BaseEntity {

    @Column(unique = true)
    private String appCode;
    private String name;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Department department;

    @ManyToOne(cascade = CascadeType.DETACH)
    private User appOwner;

    @ManyToMany(cascade = {CascadeType.DETACH})
    private Set<User> appAdmins;


    @Builder
    public App(Long id, String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime, String appCode, String name, Department department, Set<User> appAdmins, User appOwner) {
        super(id, createAuthor, createTime, updateAuthor, updateTime);
        this.appCode = appCode;
        this.name = name;
        this.department = department;
        this.appAdmins = appAdmins;
        this.appOwner = appOwner;
    }

    public App(Long id) {
        super(id);
    }
}
