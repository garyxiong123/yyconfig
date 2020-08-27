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
package com.ctrip.framework.apollo.configservice.component.wrapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * @author 客户端连接
 */
public class ClientConnection {
    private static final long TIMEOUT = 60 * 1000;
    private static final ResponseEntity<List<NamespaceVersion>> NOT_MODIFIED_RESPONSE_LIST = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    /**
     * 客户端和服务器 ns名称的映射关系
     */
    private Map<String, String> normalizedNsName2OriginalNsNameMap;

    private DeferredResult<ResponseEntity<List<NamespaceVersion>>> response;//客户端的响应


    public ClientConnection() {
        response = new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE_LIST);
    }

    /**
     * 更新命名空间的映射关系
     *
     * @param originalNsName
     * @param normalizedNsName
     */
    public void updateNsNameMapping(String originalNsName, String normalizedNsName) {
        if (normalizedNsName2OriginalNsNameMap == null) {
            normalizedNsName2OriginalNsNameMap = Maps.newHashMap();
        }
        normalizedNsName2OriginalNsNameMap.put(normalizedNsName, originalNsName);
    }


    public void onTimeout(Runnable timeoutCallback) {
        response.onTimeout(timeoutCallback);
    }

    public void onCompletion(Runnable completionCallback) {
        response.onCompletion(completionCallback);
    }

    /**
     * 设置响应
     *
     * @param notification
     */
    public void setResponse(NamespaceVersion notification) {
        setResult(Lists.newArrayList(notification));
    }

    /**
     * The appNamespace name is used as a key in client side, so we have to return the original one instead of the correct one
     */
    public void setResult(List<NamespaceVersion> notifications) {
        if (normalizedNsName2OriginalNsNameMap != null) {
            notifications.stream().filter(notification -> normalizedNsName2OriginalNsNameMap.containsKey
                    (notification.getNamespaceName())).forEach(notification -> notification.setNamespaceName(
                    normalizedNsName2OriginalNsNameMap.get(notification.getNamespaceName())));
        }

        response.setResult(new ResponseEntity<>(notifications, HttpStatus.OK));
    }

    public DeferredResult<ResponseEntity<List<NamespaceVersion>>> getResponse() {
        return response;
    }
}
