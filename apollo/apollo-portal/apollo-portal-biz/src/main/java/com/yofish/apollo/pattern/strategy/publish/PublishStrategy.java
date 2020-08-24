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
package com.yofish.apollo.pattern.strategy.publish;

import com.google.gson.Gson;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.repository.Release4MainRepository;
import com.yofish.apollo.repository.ReleaseHistoryRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.NamespaceBranchService;
import com.yofish.apollo.service.ReleaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/18 下午6:06
 */
public abstract class PublishStrategy {
    protected Gson gson = new Gson();


    @Autowired
    protected ReleaseRepository releaseRepository;
    @Autowired
    protected Release4MainRepository releaseRepository4Main;

    @Autowired
    protected ReleaseHistoryRepository releaseHistoryRepository;

    @Autowired
    protected ReleaseHistoryService releaseHistoryService;

    @Autowired
    protected NamespaceBranchService namespaceBranchService;


    public Release publish(Release release) {
        return null;

    }


    protected void createReleaseHistory(Release release, Map<String, Object> operationContext, int releaseOperation) {
        Release previousRelease = release.getPreviousRelease();
        releaseHistoryService.createReleaseHistory(release.getAppEnvClusterNamespace().getId(),release, previousRelease, releaseOperation, operationContext);

    }

    protected Release createReleaseAndUnlock(Release release) {
        releaseRepository.save(release);
        return release;
    }


}
