package com.yofish.apollo.entity.vo;


import common.dto.ClusterDTO;
import framework.apollo.core.enums.Env;
import lombok.Data;

import java.util.List;

@Data
public class EnvClusterInfo {
    private Env env;
    private List<ClusterDTO> clusters;

    public EnvClusterInfo(Env env) {
        this.env = env;
    }


}
