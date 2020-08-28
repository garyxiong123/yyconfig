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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yofish.yyconfig.common.framework.apollo.tracer.Tracer;
import lombok.Data;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Jason Song(song_s@ctrip.com)
 * @Description: 客户端 通知的消息  namespaces的版本 集合
 */
@Data
public class LongNamespaceVersion {
    public static Gson gson = new Gson();

    /**
     * key = 项目+集群+环境+namespace，   value id
     * 这里之所以用Map去管理，核心原因=》   如果是 关联的命名空间  就需要存在 appCode + appCode(public) 中的release， 所以要记录 两者的releaseMsgId
     */
    private Map<String, Long> longNsVersionMap;

    public LongNamespaceVersion() {
        this(Maps.<String, Long>newHashMap());
    }

    private LongNamespaceVersion(Map<String, Long> longNsVersionMap) {
        this.longNsVersionMap = longNsVersionMap;
    }

    public static LongNamespaceVersion buildLongNamespaceVersion(String longNsVersionMapString) {
        Gson gson = new Gson();
        LongNamespaceVersion longNamespaceVersion = null;
        if (!isNullOrEmpty(longNsVersionMapString)) {
            try {
                longNamespaceVersion = gson.fromJson(longNsVersionMapString, LongNamespaceVersion.class);
            } catch (Throwable ex) {
                Tracer.logError(ex);
            }
        }

        return longNamespaceVersion;
    }

    /**
     * 消息合并
     *
     * @param longNamespaceVersion
     */

    public void mergeFrom(LongNamespaceVersion longNamespaceVersion) {
        if (longNamespaceVersion == null) {
            return;
        }

        for (Map.Entry<String, Long> entry : longNamespaceVersion.getLongNsVersionMap().entrySet()) {
            //to make sure the notification id always grows bigger
            if (this.has(entry.getKey()) && this.get(entry.getKey()) >= entry.getValue()) {
                continue;
            }
            this.put(entry.getKey(), entry.getValue());
        }
    }


    public void put(String key, long notificationId) {
        longNsVersionMap.put(key, notificationId);
    }

    public Long get(String key) {
        return this.longNsVersionMap.get(key);
    }

    public boolean has(String key) {
        return this.longNsVersionMap.containsKey(key);
    }

    public boolean isEmpty() {
        return this.longNsVersionMap.isEmpty();
    }


    public LongNamespaceVersion clone() {
        return new LongNamespaceVersion(ImmutableMap.copyOf(this.longNsVersionMap));
    }
}
