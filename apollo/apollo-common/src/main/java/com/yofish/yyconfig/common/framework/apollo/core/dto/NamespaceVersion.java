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
package com.yofish.yyconfig.common.framework.apollo.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * namespace 版本 变更通知
 */
@Data
public class NamespaceVersion implements Serializable {

    private String namespaceName;
    /**
     * 发布消息的版本Id
     */
    private long releaseMessageId;

    private volatile LongNamespaceVersion longNamespaceVersion;

    //for json converter
    public NamespaceVersion() {
    }

    public NamespaceVersion(String namespaceName, long releaseMessageId) {
        this.namespaceName = namespaceName;
        this.releaseMessageId = releaseMessageId;
    }

    public void addMessage(String key, long notificationId) {
        if (this.longNamespaceVersion == null) {
            synchronized (this) {
                if (this.longNamespaceVersion == null) {
                    this.longNamespaceVersion = new LongNamespaceVersion();
                }
            }
        }
        this.longNamespaceVersion.put(key, notificationId);
    }

    @Override
    public String toString() {
        return "NamespaceVersion{" +
                "namespaceName='" + namespaceName + '\'' +
                ", notificationId=" + releaseMessageId +
                '}';
    }
}
