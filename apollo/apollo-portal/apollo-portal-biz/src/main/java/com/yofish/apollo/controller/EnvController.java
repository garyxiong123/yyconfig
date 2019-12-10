package com.yofish.apollo.controller;

import com.yofish.apollo.service.ServerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/envs")
public class EnvController {

    @Autowired
    private ServerConfigService serverConfigService;

    @GetMapping
    public List<String> envs() {
        return this.serverConfigService.getActiveEnvs();
    }

}
