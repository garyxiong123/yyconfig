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
package com.yofish.gary.biz.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Department extends BaseEntity {

    private String code;

    private String name;

    private String comment;

    public Department(Long id) {
        super(id);
    }

    @Builder
    public Department(Long id, String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime, String code, String name, String comment) {
        super(id, createAuthor, createTime, updateAuthor, updateTime);
        this.code = code;
        this.name = name;
        this.comment = comment;
    }
}
