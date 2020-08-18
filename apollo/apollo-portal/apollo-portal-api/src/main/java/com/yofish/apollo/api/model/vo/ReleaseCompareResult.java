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
package com.yofish.apollo.api.model.vo;


import com.yofish.apollo.api.enums.ChangeType;
import com.yofish.apollo.api.model.bo.KVEntity;
import common.entity.EntityPair;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class ReleaseCompareResult {

    private List<Change> changes = new LinkedList<>();

    public void addEntityPair(ChangeType type, KVEntity firstEntity, KVEntity secondEntity) {
        changes.add(new Change(type, new EntityPair<>(firstEntity, secondEntity)));
    }

    public boolean hasContent() {
        return !changes.isEmpty();
    }

}
