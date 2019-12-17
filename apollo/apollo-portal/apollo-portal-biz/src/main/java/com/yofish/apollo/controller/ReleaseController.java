package com.yofish.apollo.controller;

import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.dto.ReleaseDTO;
import com.yofish.apollo.listener.ConfigPublishEvent;
import com.yofish.apollo.model.bo.ReleaseBO;
import com.yofish.apollo.model.model.NamespaceReleaseModel;
import com.yofish.apollo.model.vo.ReleaseCompareResult;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.apollo.service.ReleaseService;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import common.utils.RequestPrecondition;
import framework.apollo.core.enums.Env;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Objects;

import static common.utils.RequestPrecondition.checkModel;


@RestController
public class ReleaseController {

    @Autowired
    private ReleaseService releaseService;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private PortalConfig portalConfig;
    @Autowired
    private PermissionValidator permissionValidator;
    @Autowired
    private AppEnvClusterNamespaceRepository appEnvClusterNamespaceRepository;

    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/releases", method = RequestMethod.POST)
    public ReleaseDTO createRelease(@RequestBody NamespaceReleaseModel namespaceReleaseModel) {

        checkModel(Objects.nonNull(namespaceReleaseModel));
        AppEnvClusterNamespace appEnvClusterNamespace = appEnvClusterNamespaceRepository.findById(namespaceReleaseModel.getAppEnvClusterNamespaceId()).get();
        if (namespaceReleaseModel.isEmergencyPublish() && !portalConfig.isEmergencyPublishAllowed(Env.valueOf(appEnvClusterNamespace.getAppEnvCluster().getEnv()))) {
            throw new BadRequestException(String.format("Env: %s is not supported emergency publish now", null));
        }

        Release publish = releaseService.publish(appEnvClusterNamespace, namespaceReleaseModel.getReleaseTitle(), namespaceReleaseModel.getReleaseComment(), namespaceReleaseModel.getReleasedBy(), namespaceReleaseModel.isEmergencyPublish());
        ReleaseDTO createdRelease = transformRelease2Dto(publish);
        ConfigPublishEvent event = ConfigPublishEvent.instance();
//        event.withAppId(appId)
//                .withCluster(clusterName)
//                .withNamespace(namespaceName)
//                .withReleaseId(createdRelease.getId())
//                .setNormalPublishEvent(true)
//                .setEnv(Env.valueOf(env));

        publisher.publishEvent(event);

        return createdRelease;
    }

    private ReleaseDTO transformRelease2Dto(Release publish) {
        return null;
    }

    @PreAuthorize(value = "@permissionValidator.hasReleaseNamespacePermission(#appId, #namespaceName, #env)")
    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/branches/{branchName}/releases",
            method = RequestMethod.POST)
    public ReleaseDTO createGrayRelease(@PathVariable String appId,
                                        @PathVariable String env, @PathVariable String clusterName,
                                        @PathVariable String namespaceName, @PathVariable String branchName,
                                        @RequestBody NamespaceReleaseModel model) {

        checkModel(Objects.nonNull(model));
//        model.setAppId(appId);
//        model.setEnv(env);
//        model.setClusterName(branchName);
//        model.setNamespaceName(namespaceName);

        if (model.isEmergencyPublish() && !portalConfig.isEmergencyPublishAllowed(Env.valueOf(env))) {
            throw new BadRequestException(String.format("Env: %s is not supported emergency publish now", env));
        }

        Release createdRelease = releaseService.publish(null,  model.getReleaseTitle(),  model.getReleaseComment(),  null,  model.isEmergencyPublish());
        ReleaseDTO releaseDTO = transformRelease2Dto(createdRelease);
        ConfigPublishEvent event = ConfigPublishEvent.instance();
        event.withAppId(appId)
                .withCluster(clusterName)
                .withNamespace(namespaceName)
                .withReleaseId(createdRelease.getId())
                .setGrayPublishEvent(true)
                .setEnv(Env.valueOf(env));

        publisher.publishEvent(event);

        return releaseDTO;
    }


    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/releases/all", method = RequestMethod.GET)
    public List<ReleaseBO> findAllReleases(@PathVariable String appId,
                                           @PathVariable String env,
                                           @PathVariable String clusterName,
                                           @PathVariable String namespaceName,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "5") int size) {

        RequestPrecondition.checkNumberPositive(size);
        RequestPrecondition.checkNumberNotNegative(page);

        return releaseService.findAllReleases(appId, Env.valueOf(env), clusterName, namespaceName, page, size);
    }

    @RequestMapping(value = "/apps/{appId}/envs/{env}/clusters/{clusterName}/namespaces/{namespaceName}/releases/active", method = RequestMethod.GET)
    public List<ReleaseDTO> findActiveReleases(@PathVariable String appId,
                                               @PathVariable String env,
                                               @PathVariable String clusterName,
                                               @PathVariable String namespaceName,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size) {

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
                         @PathVariable long releaseId) throws AccessDeniedException {
        ReleaseDTO release = releaseService.findReleaseById(Env.valueOf(env), releaseId);

        if (release == null) {
            throw new NotFoundException("release not found");
        }

        if (!permissionValidator.hasReleaseNamespacePermission(release.getAppId())) {
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
}
