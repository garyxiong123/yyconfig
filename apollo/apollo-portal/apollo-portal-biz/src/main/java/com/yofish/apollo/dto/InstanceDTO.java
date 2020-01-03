package com.yofish.apollo.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InstanceDTO {

    private Long id;

    private String appId;

    private String clusterName;

    private String dataCenter;

    private String ip;

    private List<InstanceConfigDTO> configs;

    private Date dataChangeCreatedTime;

}
