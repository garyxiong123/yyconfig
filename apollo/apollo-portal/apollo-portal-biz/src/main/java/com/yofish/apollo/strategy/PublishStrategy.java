package com.yofish.apollo.strategy;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import com.yofish.apollo.domain.GrayReleaseRule;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.Release4Branch;
import com.yofish.apollo.repository.Release4MainRepository;
import com.yofish.apollo.repository.ReleaseHistoryRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.NamespaceBranchService;
import com.yofish.apollo.service.ReleaseHistoryService;
import common.constants.GsonType;
import common.constants.ReleaseOperationContext;
import common.utils.GrayReleaseRuleItemTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
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


    public Release publish(Release release4Main) {
        return null;

    }







    protected void createReleaseHistory(Release release) {
//        Release previousRelease = release4Branch.getPreviousRelease();
////        releaseHistoryService.createReleaseHistory(release4Branch, previousRelease, releaseOperation, releaseOperationContext);

    }

    protected Release createReleaseAndUnlock(Release release) {
        releaseRepository.save(release);
        return release;
    }







}
