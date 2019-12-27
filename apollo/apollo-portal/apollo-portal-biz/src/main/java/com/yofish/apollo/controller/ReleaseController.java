package com.yofish.apollo.controller;

import com.yofish.apollo.component.AppPreAuthorize;
import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Branch;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.listener.ConfigPublishEvent;
import com.yofish.apollo.message.Topics;
import com.yofish.apollo.model.bo.ReleaseBO;
import com.yofish.apollo.model.model.NamespaceReleaseModel;
import com.yofish.apollo.model.vo.ReleaseCompareResult;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.ReleaseMessageRepository;
import com.yofish.apollo.service.PortalConfig;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.apollo.util.ReleaseMessageKeyGenerator;
import com.yofish.gary.api.enums.UpmsResultCode;
import com.youyu.common.api.Result;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyAssert;
import common.dto.ReleaseDTO;
import common.utils.RequestPrecondition;
import framework.apollo.core.enums.Env;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static common.utils.RequestPrecondition.checkModel;


@RestController(value = "/release")
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
    @Autowired
    private ReleaseMessageRepository messageRepository;

    @RequestMapping(value = "/createRelease", method = RequestMethod.POST)
    public Result<ReleaseDTO> createRelease(@RequestBody NamespaceReleaseModel namespaceReleaseModel) {

        checkModel(Objects.nonNull(namespaceReleaseModel));
        AppEnvClusterNamespace namespace = appEnvClusterNamespaceRepository.findById(namespaceReleaseModel.getAppEnvClusterNamespaceId()).orElseGet(() -> {throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG,"项目id不存在");} );
        if (namespaceReleaseModel.isEmergencyPublish() && !portalConfig.isEmergencyPublishAllowed(Env.valueOf(namespace.getAppEnvCluster().getEnv()))) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("Env: %s is not supported emergency publish now", null));
        }

        Release publish = releaseService.publish(namespace, namespaceReleaseModel.getReleaseTitle(), namespaceReleaseModel.getReleaseComment(), null, namespaceReleaseModel.isEmergencyPublish());
        ReleaseDTO createdRelease = transformRelease2Dto(publish);
        ConfigPublishEvent event = ConfigPublishEvent.instance();
//        event.withAppId(appId)
//                .withCluster(clusterName)
//                .withNamespace(namespaceName)
//                .withReleaseId(createdRelease.getId())
//                .setNormalPublishEvent(true)
//                .setEnv(Env.valueOf(env));

        publisher.publishEvent(event);

        ReleaseMessage releaseMessage = new ReleaseMessage(namespace);
        messageRepository.save(releaseMessage);

        return Result.ok(createdRelease);
    }

    private ReleaseDTO transformRelease2Dto(Release publish) {
        return null;
    }

//    @PreAuthorize(value = "@permissionValidator.hasReleaseNamespacePermission(#appId, #namespaceName, #env)")
    @RequestMapping(value = "/createGrayRelease", method = RequestMethod.POST)
    public Result<ReleaseDTO> createGrayRelease(@RequestBody NamespaceReleaseModel model) {

        checkModel(Objects.nonNull(model));
//        model.setAppId(appId);
//        model.setEnv(env);
//        model.setClusterName(branchName);
//        model.setNamespaceName(namespaceName);

//        if (model.isEmergencyPublish() && !portalConfig.isEmergencyPublishAllowed(Env.valueOf(env))) {
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("Env: %s is not supported emergency publish now", env));
//        }

        Release createdRelease = releaseService.publish(null, model.getReleaseTitle(), model.getReleaseComment(), null, model.isEmergencyPublish());
        ReleaseDTO releaseDTO = transformRelease2Dto(createdRelease);
        ConfigPublishEvent event = ConfigPublishEvent.instance();
//        event.withAppId(appId)
//                .withCluster(clusterName)
//                .withNamespace(namespaceName)
//                .withReleaseId(createdRelease.getId())
//                .setGrayPublishEvent(true)
//                .setEnv(Env.valueOf(env));

        publisher.publishEvent(event);

        return Result.ok(releaseDTO);
    }

    @AppPreAuthorize(AppPreAuthorize.Authorize.AppOwner)
    @RequestMapping(path = "/releases/{releaseId}/rollback", method = RequestMethod.PUT)
    public Result rollback(@PathVariable long releaseId)  {
        Release release = releaseService.findReleaseById(releaseId).orElseGet(() -> {throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG,"releaseId不存在");} );

        releaseService.rollback(releaseId);


        return Result.ok();
    }


    @RequestMapping(value = "/namespaceId/{namespaceId}/releases/all", method = RequestMethod.GET)
    public Result<List<ReleaseBO>> findAllReleases(@PathVariable Long namespaceId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {

        RequestPrecondition.checkNumberPositive(size);
        RequestPrecondition.checkNumberNotNegative(page);
        Pageable pageable = PageRequest.of(page, size);

        return Result.ok(releaseService.findAllReleases(namespaceId, pageable));
    }

    @RequestMapping(value = "/namespaceId/{namespaceId}/releases/active", method = RequestMethod.GET)
    public Result<List<ReleaseDTO>> findActiveReleases(@PathVariable Long namespaceId, @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "5") int size) {

        RequestPrecondition.checkNumberPositive(size);
        RequestPrecondition.checkNumberNotNegative(page);
        Pageable pageable = PageRequest.of(page, size);

        return Result.ok(releaseService.findActiveReleases(namespaceId, pageable));
    }

    @ApiOperation("发布比较：   ADDED, MODIFIED, DELETED")
    @RequestMapping(value = "/releases/compare", method = RequestMethod.GET)
    public Result<ReleaseCompareResult> compareRelease(@RequestParam long baseReleaseId, @RequestParam long toCompareReleaseId) {

        return Result.ok(releaseService.compare( baseReleaseId, toCompareReleaseId));
    }


}
