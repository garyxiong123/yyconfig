package com.yofish.apollo.controller;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Department;
import com.yofish.apollo.model.AppModel;
import com.yofish.apollo.model.bo.NamespaceBO;
import com.yofish.apollo.model.vo.EnvClusterInfo;
import com.yofish.apollo.service.AppService;
import com.yofish.apollo.service.ServerConfigService;
import com.yofish.gary.biz.domain.User;
import com.youyu.common.api.PageData;
import com.youyu.common.api.Result;
import common.exception.BadRequestException;
import common.utils.InputValidator;
import common.utils.RequestPrecondition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(description = "项目")
@RestController
@RequestMapping("/apps")
public class AppController {

    @Autowired
    private AppService appService;
    @Autowired
    private ServerConfigService serverConfigService;


    /**
     * create
     * <p>
     * hasCreateApplicationPermission
     *
     * @param appModel
     * @return
     */
    @PostMapping
    @ApiOperation("创建项目")
    public Result<App> create(@Valid @RequestBody AppModel appModel) {

        App app = transformToApp(appModel);

        App createdApp = appService.createApp(app);

        return Result.ok(createdApp);
    }


    @GetMapping("/search")
    @ApiOperation("查询项目")
    public Result<PageData<App>> searchByAppCodeOrAppName(@RequestParam(required = false) String query, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        if (StringUtils.isEmpty(query)) {
            return Result.ok(appService.findAll(pageable));
        } else {
            return Result.ok(appService.searchByAppCodeOrAppName(query, pageable));
        }
    }


    @PutMapping("/{appId:\\d+}")
    @ApiOperation("修改项目信息")
    public Result<App> update(@PathVariable Long appId, @Valid @RequestBody AppModel appModel) {
        App app = transformToApp(appModel);
        app.setId(appId);

        App updatedApp = appService.updateApp(app);
        return Result.ok(updatedApp);
    }

    @GetMapping("/{appId:\\d+}")
    @ApiOperation("查询项目信息")
    public Result<App> update(@PathVariable Long appId) {
        App app = appService.getApp(appId);
        if (app == null) {
            throw new BadRequestException("项目不存在！");
        }
        return Result.ok(app);
    }

    @GetMapping("/{appId:\\d+}/navtree")
    public Result<List<EnvClusterInfo>> nav(@PathVariable long appId) {
        List<EnvClusterInfo> envClusterInfoList = new ArrayList<>();
        List<String> envs = this.serverConfigService.getActiveEnvs();
        for (String env : envs) {
            envClusterInfoList.add(appService.createEnvNavNode(env, appId));
        }
        return Result.ok(envClusterInfoList);
    }


    @GetMapping("/apps/{appCode}/envs/{env}/clusters/{clusterName}/namespaces")
    public Result<List<NamespaceBO>> findNamespaces(@PathVariable String appCode, @PathVariable String env,
                                            @PathVariable String clusterName) {
        String jsonData = "[{\"baseInfo\":{\"id\":440,\"appCode\":\"test11\",\"clusterName\":\"default\",\"namespaceName\":\"application\",\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-09-05T13:39:13.000+0800\",\"updateTime\":\"2019-09-05T13:39:13.000+0800\"},\"itemModifiedCnt\":0,\"items\":[{\"item\":{\"id\":14187,\"namespaceId\":440,\"key\":\"123\",\"value\":\"233\",\"comment\":\"\",\"lineNum\":1,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-09-06T09:52:22.000+0800\",\"updateTime\":\"2019-09-06T09:52:22.000+0800\"},\"isModified\":false,\"isDeleted\":false},{\"item\":{\"id\":14926,\"namespaceId\":440,\"key\":\"22\",\"value\":\"12\",\"comment\":\"\",\"lineNum\":2,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-11-18T14:15:11.000+0800\",\"updateTime\":\"2019-11-18T14:15:11.000+0800\"},\"isModified\":false,\"isDeleted\":false}],\"format\":\"properties\",\"isPublic\":false,\"parentAppId\":\"test11\",\"comment\":\"default app namespace\"},{\"baseInfo\":{\"id\":442,\"appCode\":\"test11\",\"clusterName\":\"default\",\"namespaceName\":\"TS.test\",\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-09-06T09:53:41.000+0800\",\"updateTime\":\"2019-09-06T09:53:41.000+0800\"},\"itemModifiedCnt\":1,\"items\":[{\"item\":{\"id\":14188,\"namespaceId\":442,\"key\":\"123\",\"value\":\"123\",\"comment\":\"\",\"lineNum\":1,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-09-06T10:09:55.000+0800\",\"updateTime\":\"2019-09-06T10:09:55.000+0800\"},\"isModified\":true,\"isDeleted\":false,\"oldValue\":\"\",\"newValue\":\"123\"}],\"format\":\"properties\",\"isPublic\":true,\"parentAppId\":\"test11\",\"comment\":\"test\"},{\"baseInfo\":{\"id\":444,\"appCode\":\"test11\",\"clusterName\":\"default\",\"namespaceName\":\"TS.test22\",\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-09-06T10:24:40.000+0800\",\"updateTime\":\"2019-09-06T10:24:40.000+0800\"},\"itemModifiedCnt\":0,\"items\":[],\"format\":\"properties\",\"isPublic\":true,\"parentAppId\":\"test11\",\"comment\":\"test22\"},{\"baseInfo\":{\"id\":517,\"appCode\":\"test11\",\"clusterName\":\"default\",\"namespaceName\":\"mall\",\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-09T17:25:39.000+0800\",\"updateTime\":\"2019-12-09T17:25:39.000+0800\"},\"itemModifiedCnt\":3,\"items\":[{\"item\":{\"id\":15110,\"namespaceId\":517,\"key\":\"\",\"value\":\"\",\"comment\":\"#测试用的\",\"lineNum\":1,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-19T09:56:32.000+0800\",\"updateTime\":\"2019-12-19T09:56:32.000+0800\"},\"isModified\":false,\"isDeleted\":false},{\"item\":{\"id\":15111,\"namespaceId\":517,\"key\":\"zuul.SendErrorFilter.error.disable\",\"value\":\"true\",\"comment\":\"\",\"lineNum\":2,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-19T09:56:32.000+0800\",\"updateTime\":\"2019-12-19T09:56:32.000+0800\"},\"isModified\":true,\"isDeleted\":false,\"oldValue\":\"\",\"newValue\":\"true\"},{\"item\":{\"id\":15112,\"namespaceId\":517,\"key\":\"zuul.host.connect-timeout-millis\",\"value\":\"20000\",\"comment\":\"\",\"lineNum\":3,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-19T09:56:32.000+0800\",\"updateTime\":\"2019-12-19T09:56:32.000+0800\"},\"isModified\":true,\"isDeleted\":false,\"oldValue\":\"\",\"newValue\":\"20000\"},{\"item\":{\"id\":15113,\"namespaceId\":517,\"key\":\"zuul.host.socket-timeout-millis\",\"value\":\"60000\",\"comment\":\"\",\"lineNum\":4,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-19T09:56:32.000+0800\",\"updateTime\":\"2019-12-19T09:56:32.000+0800\"},\"isModified\":true,\"isDeleted\":false,\"oldValue\":\"\",\"newValue\":\"60000\"}],\"format\":\"properties\",\"isPublic\":false,\"parentAppId\":\"test11\",\"comment\":\"\"},{\"baseInfo\":{\"id\":521,\"appCode\":\"test11\",\"clusterName\":\"default\",\"namespaceName\":\"TS.upms-redis\",\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-10T16:19:45.000+0800\",\"updateTime\":\"2019-12-10T16:19:45.000+0800\"},\"itemModifiedCnt\":1,\"items\":[{\"item\":{\"id\":15115,\"namespaceId\":521,\"key\":\"spring.redis.jedis.pool.maxActive\",\"value\":\"100\",\"comment\":\"\",\"lineNum\":1,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-19T09:57:44.000+0800\",\"updateTime\":\"2019-12-19T09:57:44.000+0800\"},\"isModified\":false,\"isDeleted\":false},{\"item\":{\"id\":15116,\"namespaceId\":521,\"key\":\"spring.redis.timeout\",\"value\":\"300ms\",\"comment\":\"\",\"lineNum\":2,\"createAuthor\":\"apollo\",\"updateAuthor\":\"apollo\",\"createTime\":\"2019-12-19T09:58:24.000+0800\",\"updateTime\":\"2019-12-19T09:58:37.000+0800\"},\"isModified\":true,\"isDeleted\":false,\"oldValue\":\"\",\"newValue\":\"300ms\"}],\"format\":\"properties\",\"isPublic\":true,\"parentAppId\":\"ts-global-config\",\"comment\":\"\"}]";
        List<NamespaceBO> fromJson = new Gson().fromJson(jsonData, new TypeToken<List<NamespaceBO>>() {
        }.getType());
        return Result.ok(fromJson);
    }



    private App transformToApp(AppModel appModel) {
        String appCode = appModel.getAppCode();
        String appName = appModel.getName();
        Long ownerId = appModel.getOwnerId();
        Long orgId = appModel.getOrgId();
        Set<Long> admins = appModel.getAdmins();

        RequestPrecondition.checkArgumentsNotEmpty(appCode, appName, ownerId, orgId);

        if (!InputValidator.isValidClusterNamespace(appModel.getAppCode())) {
            throw new BadRequestException(
                    String.format("AppCode格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
        }
        return App.builder()
                .appCode(appCode)
                .name(appName)
                .department(new Department(orgId))
                .appOwner(new User(ownerId))
                .appAdmins(ObjectUtils.isEmpty(admins) ? null : admins.stream().map(userId -> new User(userId)).collect(Collectors.toSet()))
                .build();

    }
}
