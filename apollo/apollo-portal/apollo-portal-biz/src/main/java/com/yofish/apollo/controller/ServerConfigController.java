package com.yofish.apollo.controller;


import com.yofish.apollo.domain.ServerConfig;
import com.yofish.apollo.repository.ServerConfigRepository;
import com.yofish.apollo.spi.UserInfoHolder;
import common.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.isNull;

/**
 * 配置中心本身需要一些配置,这些配置放在数据库里面
 */
@RestController
public class ServerConfigController {

    @Autowired
    private ServerConfigRepository serverConfigRepository;

    //  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
    @RequestMapping(value = "/server/config", method = RequestMethod.POST)
    public ServerConfig createOrUpdate(ServerConfig serverConfig) {

//        checkModel(Objects.nonNull(serverConfig));
//        RequestPrecondition.checkArgumentsNotEmpty(serverConfig.getKey(), serverConfig.getValue());


        ServerConfig storedConfig = serverConfigRepository.findByKey(serverConfig.getKey());

        if (isNull(storedConfig)) {
            return serverConfigRepository.save(serverConfig);
        } else {//update
            BeanUtils.copyEntityProperties(serverConfig, storedConfig);
            return serverConfigRepository.save(storedConfig);
        }
    }

    //  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
    @RequestMapping(value = "/server/config/{key:.+}", method = RequestMethod.GET)
    public ServerConfig loadServerConfig(@PathVariable String key) {
        return serverConfigRepository.findByKey(key);
    }

}
