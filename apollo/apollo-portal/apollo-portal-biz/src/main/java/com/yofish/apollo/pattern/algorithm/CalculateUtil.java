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
package com.yofish.apollo.pattern.algorithm;

import com.google.gson.Gson;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.domain.Release;
import com.yofish.yyconfig.common.common.constants.GsonType;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yofish.yyconfig.common.common.utils.YyStringUtils.isEmpty;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/19 下午5:54
 */
public class CalculateUtil {

    protected static Gson gson = new Gson();


    public static Map<String, String> calculateBranchNamespaceConfigsToPublish(Release release4BranchToPublish) {
        Map<String, String> parentConfigurations = calculateMainConfigs(release4BranchToPublish);


        Map<String, String> branchConfigurations = calculateConfigs(release4BranchToPublish);

        Map<String, String> branchNamespaceToPublishConfigs = mergeConfiguration(parentConfigurations, branchConfigurations);
        return branchNamespaceToPublishConfigs;
    }


    public static Map<String, String> mergeConfiguration(Map<String, String> baseConfigurations, Map<String, String> branchConfigurations) {

        Map<String, String> result = new HashMap<>();
        //copy base configuration
        if (!CollectionUtils.isEmpty(baseConfigurations)) {
            for (Map.Entry<String, String> entry : baseConfigurations.entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        if (!CollectionUtils.isEmpty(branchConfigurations)) {
            //update and publish
            for (Map.Entry<String, String> entry : branchConfigurations.entrySet()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public static Map<String, String> calculateMainConfigs(Release release4Branch) {
        Release mainRelease = ((AppEnvClusterNamespace4Branch) release4Branch.getAppEnvClusterNamespace()).getMainNamespace().findLatestActiveRelease();
        return calculateConfigs(mainRelease);
    }

    public static Map<String, String> calculateConfigs(Release release) {
        if (release == null) {
            return null;
        }

        List<Item> items = release.getAppEnvClusterNamespace().getItems();
        Map<String, String> configurations = new HashMap<String, String>();

        for (Item item : items) {
            if (isEmpty(item.getKey())) {
                continue;
            }
            configurations.put(item.getKey(), item.getValue());
        }

        return configurations;
    }

    public static Map<String, String> Json2Map(String config) {
        return config != null ? gson.fromJson(config, GsonType.CONFIG) : new HashMap<>();
    }


}
