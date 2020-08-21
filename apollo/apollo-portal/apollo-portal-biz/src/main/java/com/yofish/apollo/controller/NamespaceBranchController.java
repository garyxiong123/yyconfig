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
package com.yofish.apollo.controller;

import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.pattern.listener.config.ConfigPublishEvent;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.yofish.apollo.model.NamespaceReleaseModel;
import com.yofish.apollo.service.NamespaceBranchService;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.yyconfig.common.common.dto.GrayReleaseRuleDTO;
import com.yofish.yyconfig.common.common.dto.NamespaceDTO;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.yofish.yyconfig.common.framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class NamespaceBranchController {
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private NamespaceBranchService namespaceBranchService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private PortalConfig portalConfig;

    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches", method = RequestMethod.GET)
    public NamespaceVO findBranch(@PathVariable Long namespaceId) {
        NamespaceVO namespaceVO = namespaceBranchService.findBranch(namespaceId);

        if (namespaceVO != null && permissionValidator.shouldHideConfigToCurrentUser()) {
            namespaceVO.hideItems();
        }

        return namespaceVO;
    }

    @PreAuthorize(value = "@permissionValidator.hasModifyNamespacePermission(#appId, #namespaceName, #env)")
    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches", method = RequestMethod.POST)
    public NamespaceDTO createBranch(@PathVariable Long namespaceId, @PathVariable String branchName) {

        return namespaceBranchService.createBranch(namespaceId, branchName);
    }

    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}", method = RequestMethod.DELETE)
    public void deleteBranch(@PathVariable String appId,
                             @PathVariable String env,
                             @PathVariable String clusterName,
                             @PathVariable String namespaceName,
                             @PathVariable String branchName) {

        boolean canDelete = permissionValidator.hasReleaseNamespacePermission(appId, namespaceName) || (permissionValidator.hasModifyNamespacePermission(appId, namespaceName, env)
        );

//        (releaseService.loadLatestRelease(appCode, Env.valueOf(env), branchName, namespaceName) == null)
        if (!canDelete) {
//            throw new AccessDeniedException("Forbidden operation. "
//                    + "Caused by: 1.you don't have release permission "
//                    + "or 2. you don't have modification permission "
//                    + "or 3. you have modification permission but branch has been released");
        }

        namespaceBranchService.deleteBranch(appId, Env.valueOf(env), clusterName, namespaceName, branchName);
        // TODO release Message
//        ReleaseMessage releaseMessage = new ReleaseMessage(namespace);
//        messageRepository.save(releaseMessage);

    }


    @PreAuthorize(value = "@permissionValidator.hasReleaseNamespacePermission(#appId, #namespaceName, #env)")
    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}/merge", method = RequestMethod.POST)
    public Release merge(@PathVariable String appId, @PathVariable String env,
                         @PathVariable String clusterName, @PathVariable String namespaceName,
                         @PathVariable String branchName, @RequestParam(value = "deleteBranch", defaultValue = "true") boolean deleteBranch,
                         @RequestBody NamespaceReleaseModel model) {

        if (model.isEmergencyPublish() && !portalConfig.isEmergencyPublishAllowed(Env.fromString(env))) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("Env: %s is not supported emergency publish now", env));
        }
        Release createdRelease = null;
//        Release createdRelease = namespaceBranchService.
//                merge(appCode, Env.valueOf(env), clusterName, namespaceName, branchName, model.getReleaseTitle(), model.getReleaseComment(),
//                model.isEmergencyPublish(), deleteBranch);

        ConfigPublishEvent event = ConfigPublishEvent.instance();
        event.withAppId(appId)
                .withCluster(clusterName)
                .withNamespace(namespaceName)
                .withReleaseId(createdRelease.getId())
                .setMergeEvent(true)
                .setEnv(Env.valueOf(env));

        publisher.publishEvent(event);

        return createdRelease;
    }


    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}/rules", method = RequestMethod.GET)
    public GrayReleaseRuleDTO getBranchGrayRules(@PathVariable Long namespaceId) {

        return namespaceBranchService.findBranchGrayRules(namespaceId);
    }


//    @PreAuthorize(value = "@permissionValidator.hasOperateNamespacePermission(#appCode, #namespaceName, #env)")
    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}/rules", method = RequestMethod.PUT)
    public void updateBranchRules(@PathVariable String appId, @PathVariable String env,
                                  @PathVariable String clusterName, @PathVariable String namespaceName,
                                  @PathVariable String branchName, @RequestBody GrayReleaseRuleDTO rules) {

        namespaceBranchService.updateBranchGrayRules(appId, Env.valueOf(env), clusterName, namespaceName, branchName, rules);

    }

}
