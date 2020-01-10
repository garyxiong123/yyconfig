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
package com.yofish.apollo.model.model;

import com.yofish.apollo.domain.Item;
import com.yofish.apollo.model.vo.NamespaceIdentifier;
import common.dto.ItemDTO;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NamespaceSyncModel {

    private List<NamespaceIdentifier> syncToNamespaces;
    private List<Item> syncItems;


    public List<NamespaceIdentifier> getSyncToNamespaces() {
        return syncToNamespaces;
    }

    public void setSyncToNamespaces(List<NamespaceIdentifier> syncToNamespaces) {
        this.syncToNamespaces = syncToNamespaces;
    }

    public List<Item> getSyncItems() {
        return syncItems;
    }

    public void setSyncItems(List<Item> syncItems) {
        this.syncItems = syncItems;
    }
}
