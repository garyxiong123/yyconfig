package com.yofish.apollo.pattern.algorithm;

import com.google.gson.Gson;
import com.yofish.apollo.api.enums.ChangeType;
import com.yofish.apollo.api.model.bo.KVEntity;
import com.yofish.apollo.api.model.vo.ReleaseCompareResult;
import com.yofish.apollo.domain.Release;
import common.constants.GsonType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 发布配置 比较算法
 * @Date: 2020/7/27 下午12:51
 */
@Component
public class ReleaseCompareAlgorithm {
    private Gson gson = new Gson();

    public ReleaseCompareResult releaseCompare(Release baseRelease, Release toCompareRelease) {

        Map<String, String> baseReleaseConfiguration = baseRelease == null ? new HashMap<>() : gson.fromJson(baseRelease.getConfigurations(), GsonType.CONFIG);
        Map<String, String> toCompareReleaseConfiguration = toCompareRelease == null ? new HashMap<>() : gson.fromJson(toCompareRelease.getConfigurations(), GsonType.CONFIG);

        ReleaseCompareResult compareResult = new ReleaseCompareResult();

        //added and modified in firstRelease
        for (Map.Entry<String, String> entry : baseReleaseConfiguration.entrySet()) {
            String key = entry.getKey();
            String firstValue = entry.getValue();
            String secondValue = toCompareReleaseConfiguration.get(key);
            //added
            if (secondValue == null) {
                compareResult.addEntityPair(ChangeType.DELETED, new KVEntity(key, firstValue),
                        new KVEntity(key, null));
            } else if (!com.google.common.base.Objects.equal(firstValue, secondValue)) {
                compareResult.addEntityPair(ChangeType.MODIFIED, new KVEntity(key, firstValue),
                        new KVEntity(key, secondValue));
            }

        }

        //deleted in firstRelease
        for (Map.Entry<String, String> entry : toCompareReleaseConfiguration.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (baseReleaseConfiguration.get(key) == null) {
                compareResult
                        .addEntityPair(ChangeType.ADDED, new KVEntity(key, ""), new KVEntity(key, value));
            }

        }

        return compareResult;
    }

}
