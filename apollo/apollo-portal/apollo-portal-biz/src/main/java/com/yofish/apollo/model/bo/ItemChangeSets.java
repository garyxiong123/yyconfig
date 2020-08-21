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
package com.yofish.apollo.model.bo;


import com.yofish.apollo.domain.Item;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * storage cud result
 */
@Data
public class ItemChangeSets extends BaseEntity {

    private List<Item> createItems = new LinkedList<>();
    private List<Item> updateItems = new LinkedList<>();
    private List<Item> deleteItems = new LinkedList<>();

    public void addCreateItem(Item item) {
        createItems.add(item);
    }

    public void addUpdateItem(Item item) {
        updateItems.add(item);
    }

    public void addDeleteItem(Item item) {
        deleteItems.add(item);
    }

    public boolean isEmpty() {
        return createItems.isEmpty() && updateItems.isEmpty() && deleteItems.isEmpty();
    }



}
