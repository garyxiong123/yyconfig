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
package com.yofish.apollo.model.grayReleaseRule;


import com.yofish.yyconfig.common.common.dto.GrayReleaseRuleItemDTO;
import lombok.Data;

import java.util.Set;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Data
public class GrayReleaseRuleCache {
    private long ruleId;
    private String branchName;
    private String namespaceName;
    private long releaseId;
    private long loadVersion;
    private int branchStatus;
    private Set<GrayReleaseRuleItemDTO> ruleItems;

    public GrayReleaseRuleCache(long ruleId, String branchName, String namespaceName, long
            releaseId, int branchStatus, long loadVersion, Set<GrayReleaseRuleItemDTO> ruleItems) {
        this.ruleId = ruleId;
        this.branchName = branchName;
        this.namespaceName = namespaceName;
        this.releaseId = releaseId;
        this.branchStatus = branchStatus;
        this.loadVersion = loadVersion;
        this.ruleItems = ruleItems;
    }

    public boolean matches(String clientAppId, String clientIp) {
        for (GrayReleaseRuleItemDTO ruleItem : ruleItems) {
            if (ruleItem.matches(clientAppId, clientIp)) {
                return true;
            }
        }
        return false;
    }
}
