package com.yofish.apollo.controller;

import com.google.common.base.Splitter;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceController {

    private static final Splitter RELEASES_SPLITTER = Splitter.on(",").omitEmptyStrings()
        .trimResults();
/*

    @Autowired
    private InstanceService instanceService;
    @Autowired
    private ReleaseService releaeService;

    @RequestMapping(value = "/envs/{env}/instances/by-release", method = RequestMethod.GET)
    public PageDTO<Instance> getByRelease(@PathVariable String env, @RequestParam long releaseId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);

            Release release = releaeService.findOne(releaseId);
    if (release == null) {
      throw new NotFoundException(String.format("release not found for %s", releaseId));
    }
    Page<InstanceConfig> instanceConfigsPage = instanceService.findActiveInstanceConfigsByReleaseKey
        (release.getReleaseKey(), pageable);

    List<InstanceDTO> instanceDTOs = Collections.emptyList();

    if (instanceConfigsPage.hasContent()) {
      Multimap<Long, InstanceConfig> instanceConfigMap = HashMultimap.create();
      Set<String> otherReleaseKeys = Sets.newHashSet();

      for (InstanceConfig instanceConfig : instanceConfigsPage.getContent()) {
        instanceConfigMap.put(instanceConfig.getInstanceId(), instanceConfig);
        otherReleaseKeys.add(instanceConfig.getReleaseKey());
      }

      Set<Long> instanceIds = instanceConfigMap.keySet();

      List<Instance> instances = instanceService.findInstancesByIds(instanceIds);

      if (!CollectionUtils.isEmpty(instances)) {
        instanceDTOs = BeanUtils.batchTransform(InstanceDTO.class, instances);
      }

      for (InstanceDTO instanceDTO : instanceDTOs) {
        Collection<InstanceConfig> configs = instanceConfigMap.get(instanceDTO.getId());
        List<InstanceConfigDTO> configDTOs = configs.stream().map(instanceConfig -> {
          InstanceConfigDTO instanceConfigDTO = new InstanceConfigDTO();
          //to save some space
          instanceConfigDTO.setRelease(null);
          instanceConfigDTO.setReleaseDeliveryTime(instanceConfig.getReleaseDeliveryTime());
          instanceConfigDTO.setDataChangeLastModifiedTime(instanceConfig
              .getDataChangeLastModifiedTime());
          return instanceConfigDTO;
        }).collect(Collectors.toList());
        instanceDTO.setConfigs(configDTOs);
      }
    }

    return new PageDTO(instanceDTOs, pageable, instanceConfigsPage.getTotalElements());
  }

//        return instanceService.getByRelease(Env.valueOf(env), releaseId, page, size);
//    }

    @RequestMapping(value = "/envs/{env}/instances/by-namespace", method = RequestMethod.GET)
    public Page<Instance> getByNamespace(@PathVariable String env, @RequestParam String appId,
                                         @RequestParam String clusterName, @RequestParam String namespaceName,
                                         @RequestParam(required = false) String instanceAppId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "20") int size) {

        return instanceService.getByNamespace(Env.valueOf(env), appId, clusterName, namespaceName, instanceAppId, page, size);
    }

    @RequestMapping(value = "/envs/{env}/instances/by-namespace/count", method = RequestMethod.GET)
    public ResponseEntity<Number> getInstanceCountByNamespace(@PathVariable String env, @RequestParam String appId,
                                                              @RequestParam String clusterName,
                                                              @RequestParam String namespaceName) {

        int count = instanceService.getInstanceCountByNamepsace(appId, Env.valueOf(env), clusterName, namespaceName);
        return ResponseEntity.ok(new Number(count));
    }

    @RequestMapping(value = "/envs/{env}/instances/by-namespace-and-releases-not-in", method = RequestMethod.GET)
    public List<Instance> getByReleasesNotIn(@PathVariable String env, @RequestParam String appId,
                                             @RequestParam String clusterName, @RequestParam String namespaceName,
                                             @RequestParam String releaseIds) {

        Set<Long> releaseIdSet = RELEASES_SPLITTER.splitToList(releaseIds).stream().map(Long::parseLong)
            .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(releaseIdSet)) {
            throw new BadRequestException("release ids can not be empty");
        }

        return instanceService.getByReleasesNotIn(Env.valueOf(env), appId, clusterName, namespaceName, releaseIdSet);
    }
*/


}
