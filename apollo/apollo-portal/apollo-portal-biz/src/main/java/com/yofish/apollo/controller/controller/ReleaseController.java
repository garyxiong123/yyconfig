package com.yofish.apollo.controller.controller;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.entity.bo.ReleaseBO;
import com.yofish.apollo.entity.model.NamespaceReleaseModel;
import common.exception.BadRequestException;
import common.utils.RequestPrecondition;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static common.utils.RequestPrecondition.checkModel;


@RestController
public class ReleaseController {
/*

  @Autowired
  private ReleaseService releaseService;
  @Autowired
  private ApplicationEventPublisher publisher;
  @Autowired
  private PortalConfig portalConfig;
  @Autowired
  private PermissionValidator permissionValidator;
  @Autowired
  private MessageSender messageSender;

  @PreAuthorize(value = "@permissionValidator.hasReleaseNamespacePermission(#appId, #namespaceName, #env)")
  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/releases", method = RequestMethod.POST)
  public Release createRelease(@PathVariable String appId,
                               @PathVariable String env, @PathVariable String clusterName,
                               @PathVariable String namespaceName, @RequestBody NamespaceReleaseModel namespaceReleaseModel) {

    checkModel(Objects.nonNull(namespaceReleaseModel));
    namespaceReleaseModel.setAppId(appId);
    namespaceReleaseModel.setEnv(env);
    namespaceReleaseModel.setClusterName(clusterName);
    namespaceReleaseModel.setNamespaceName(namespaceName);

    if (namespaceReleaseModel.isEmergencyPublish() && !portalConfig.isEmergencyPublishAllowed(Env.valueOf(env))) {
      throw new BadRequestException(String.format("Env: %s is not supported emergency publish now", env));
    }

    Release createdRelease = releaseService.publish(namespaceReleaseModel);


      //通知发布
    ConfigPublishEvent event = ConfigPublishEvent.instance();
    event.withAppId(appId)
        .withCluster(clusterName)
        .withNamespace(namespaceName)
        .withReleaseId(createdRelease.getId())
        .setNormalPublishEvent(true)
        .setEnv(Env.valueOf(env));

    publisher.publishEvent(event);
    //发送 发布消息的历史记录
    messageSender.sendMessage(ReleaseMessageKeyGenerator.generate(appId, clusterName, namespaceName),
            Topics.APOLLO_RELEASE_TOPIC);

    return createdRelease;
  }

  @PreAuthorize(value = "@permissionValidator.hasReleaseNamespacePermission(#appId, #namespaceName, #env)")
  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}/releases",
      method = RequestMethod.POST)
  public Release createGrayRelease(@PathVariable String appId,
                                   @PathVariable String env, @PathVariable String clusterName,
                                   @PathVariable String namespaceName, @PathVariable String branchName,
                                   @RequestBody NamespaceReleaseModel model) {

    checkModel(Objects.nonNull(model));
    model.setAppId(appId);
    model.setEnv(env);
    model.setClusterName(branchName);
    model.setNamespaceName(namespaceName);

    if (model.isEmergencyPublish() && !portalConfig.isEmergencyPublishAllowed(Env.valueOf(env))) {
      throw new BadRequestException(String.format("Env: %s is not supported emergency publish now", env));
    }

    Release createdRelease = releaseService.publish(model);

    ConfigPublishEvent event = ConfigPublishEvent.instance();
    event.withAppId(appId)
        .withCluster(clusterName)
        .withNamespace(namespaceName)
        .withReleaseId(createdRelease.getId())
        .setGrayPublishEvent(true)
        .setEnv(Env.valueOf(env));

    publisher.publishEvent(event);

    return createdRelease;
  }


  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/releases/all", method = RequestMethod.GET)
  public List<ReleaseBO> findAllReleases(@PathVariable String appId,
                                         @PathVariable String env,
                                         @PathVariable String clusterName,
                                         @PathVariable String namespaceName,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size) {
    if (permissionValidator.shouldHideConfigToCurrentUser(appId, env, namespaceName)) {
      return Collections.emptyList();
    }

    RequestPrecondition.checkNumberPositive(size);
    RequestPrecondition.checkNumberNotNegative(page);

    return releaseService.findAllReleases(appId, Env.valueOf(env), clusterName, namespaceName, page, size);
  }

  @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/releases/active", method = RequestMethod.GET)
  public List<Release> findActiveReleases(@PathVariable String appId,
                                             @PathVariable String env,
                                             @PathVariable String clusterName,
                                             @PathVariable String namespaceName,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size) {

    if (permissionValidator.shouldHideConfigToCurrentUser(appId, env, namespaceName)) {
      return Collections.emptyList();
    }

    RequestPrecondition.checkNumberPositive(size);
    RequestPrecondition.checkNumberNotNegative(page);

    return releaseService.findActiveReleases(appId, Env.valueOf(env), clusterName, namespaceName, page, size);
  }

  @RequestMapping(value = "/envs/{env}/releases/compare", method = RequestMethod.GET)
  public ReleaseCompareResult compareRelease(@PathVariable String env,
                                             @RequestParam long baseReleaseId,
                                             @RequestParam long toCompareReleaseId) {

    return releaseService.compare(Env.valueOf(env), baseReleaseId, toCompareReleaseId);
  }


  @RequestMapping(path = "/envs/{env}/releases/{releaseId}/rollback", method = RequestMethod.PUT)
  public void rollback(@PathVariable String env,
                       @PathVariable long releaseId) {
    Release release = releaseService.findReleaseById(Env.valueOf(env), releaseId);

    if (release == null) {
      throw new NotFoundException("release not found");
    }

    if (!permissionValidator.hasReleaseNamespacePermission(release.getAppId(), release.getNamespaceName(), env)) {
      throw new AccessDeniedException("Access is denied");
    }

    releaseService.rollback(Env.valueOf(env), releaseId);

    ConfigPublishEvent event = ConfigPublishEvent.instance();
    event.withAppId(release.getAppId())
        .withCluster(release.getClusterName())
        .withNamespace(release.getNamespaceName())
        .withPreviousReleaseId(releaseId)
        .setRollbackEvent(true)
        .setEnv(Env.valueOf(env));

    publisher.publishEvent(event);
  }
*/


//
//  @Transactional
//  @RequestMapping(path = "/apps/{appId}/clusters/{clusterName}/namespaces/{namespaceName}/updateAndPublish", method = RequestMethod.POST)
//  public Release updateAndPublish(@PathVariable("appId") String appId,
//                                     @PathVariable("clusterName") String clusterName,
//                                     @PathVariable("namespaceName") String namespaceName,
//                                     @RequestParam("releaseName") String releaseName,
//                                     @RequestParam("branchName") String branchName,
//                                     @RequestParam(value = "deleteBranch", defaultValue = "true") boolean deleteBranch,
//                                     @RequestParam(name = "releaseComment", required = false) String releaseComment,
//                                     @RequestParam(name = "isEmergencyPublish", defaultValue = "false") boolean isEmergencyPublish,
//                                     @RequestBody ItemChangeSets changeSets) {
//    Namespace namespace = namespaceService.findOne(appId, clusterName, namespaceName);
//    if (namespace == null) {
//      throw new NotFoundException(String.format("Could not find namespace for %s %s %s", appId,
//              clusterName, namespaceName));
//    }
//
//    Release release = releaseService.mergeBranchChangeSetsAndRelease(namespace, branchName, releaseName,
//            releaseComment, isEmergencyPublish, changeSets);
//
//    if (deleteBranch) {
//      namespaceBranchService.deleteBranch(appId, clusterName, namespaceName, branchName,
//              NamespaceBranchStatus.MERGED, changeSets.getDataChangeLastModifiedBy());
//    }
//
//    messageSender.sendMessage(ReleaseMessageKeyGenerator.generate(appId, clusterName, namespaceName),
//            Topics.APOLLO_RELEASE_TOPIC);
//
//    return BeanUtils.transfrom(ReleaseDTO.class, release);
//
//  }

}
