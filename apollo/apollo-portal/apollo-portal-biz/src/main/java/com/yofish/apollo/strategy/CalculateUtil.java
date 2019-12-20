package com.yofish.apollo.strategy;

import com.google.gson.Gson;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import com.yofish.apollo.domain.Release;
import common.constants.GsonType;

import java.util.HashMap;
import java.util.Map;

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
        for (Map.Entry<String, String> entry : baseConfigurations.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }

        //update and publish
        for (Map.Entry<String, String> entry : branchConfigurations.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static Map<String, String> calculateMainConfigs(Release release4Branch) {
        Release parentLatestRelease = ((AppEnvClusterNamespace4Branch) release4Branch.getAppEnvClusterNamespace()).getMainNamespace().findLatestActiveRelease();
        return Json2Map(parentLatestRelease.getConfigurations());
    }

    public static Map<String, String> calculateConfigs(Release release) {
        if(release == null){return null;}
        Release latestActiveRelease = release.getAppEnvClusterNamespace().findLatestActiveRelease();
        if(latestActiveRelease == null){return null;}
        return Json2Map(latestActiveRelease.getConfigurations());
    }

    public static Map<String, String> Json2Map(String config) {
        return config != null ? gson.fromJson(config, GsonType.CONFIG) : new HashMap<>();
    }


}
