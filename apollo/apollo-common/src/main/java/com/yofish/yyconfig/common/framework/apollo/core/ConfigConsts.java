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
package com.yofish.yyconfig.common.framework.apollo.core;

public interface ConfigConsts {
    String APOLLO_APP_ID = "app.id";
    String APOLLO_CACHE_DIR = "apollo.cacheDir";
    String NAMESPACE_APPLICATION = "application";
    String CLUSTER_NAME_DEFAULT = "default";
    String CLUSTER_NAMESPACE_SEPARATOR = "+";
    String APOLLO_CLUSTER_KEY = "apollo.cluster";
    String YYCONFIG_META_KEY = "yyconfig.meta";
    String CONFIG_FILE_CONTENT_KEY = "content";
    String NO_APPID_PLACEHOLDER = "ApolloNoAppIdPlaceHolder";
    long NOTIFICATION_ID_PLACEHOLDER = -1;
    String CONFIG_SERVER_URL = "apollo.meta";
}
