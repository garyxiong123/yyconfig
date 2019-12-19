package com.yofish.apollo.model.vo;


import com.yofish.apollo.domain.AppEnvCluster;
import lombok.Data;

import java.util.List;

@Data
public class EnvClusterInfo {
    private String env;
    private List<AppEnvCluster> clusters;

    public EnvClusterInfo(String env) {
        this.env = env;
    }


}
