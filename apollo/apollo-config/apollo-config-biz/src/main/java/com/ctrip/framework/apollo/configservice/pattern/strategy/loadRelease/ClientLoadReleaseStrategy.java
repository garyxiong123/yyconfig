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
package com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease;


import com.ctrip.framework.apollo.configservice.domain.ConfigClient4NamespaceReq;
import com.yofish.apollo.domain.Release;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;

/**
 * @author Jason Song(song_s@ctrip.com)
 * @Description: 客户端加载配置策略
 */
public interface ClientLoadReleaseStrategy {


    Release loadRelease4Client(ConfigClient4NamespaceReq configClient4NamespaceReq);
}
