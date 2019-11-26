package com.yofish.apollo.controller;

import com.yofish.apollo.domain.Release;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/18 下午2:57
 */
public class ReleaseController {


//    public Release createRelease(@PathVariable String appId,
//                                 @PathVariable String env, @PathVariable String clusterName,
//                                 @PathVariable String namespaceName, @RequestBody NamespaceReleaseModel namespaceReleaseModel) {
//        return null;
//
//    }

//
//    @RequestMapping(value = "/envs/{env}/releases/compare", method = RequestMethod.GET)
//    public ReleaseCompareResult compareRelease(@PathVariable String env,
//                                               @RequestParam long baseReleaseId,
//                                               @RequestParam long toCompareReleaseId) {
//
////        return releaseService.compare(Env.valueOf(env), baseReleaseId, toCompareReleaseId);
//    }


    @RequestMapping(path = "/envs/{env}/releases/{releaseId}/rollback", method = RequestMethod.PUT)
    public void rollback(@PathVariable String env,
                         @PathVariable long releaseId) {
        return;

    }
}
