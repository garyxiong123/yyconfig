package com.yofish.apollo.util;


import com.yofish.apollo.domain.AppEnvClusterNamespace;
import common.utils.UniqueKeyGenerator;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ReleaseKeyGenerator extends UniqueKeyGenerator {


  /**
   * Generate the release key in the format: timestamp+appId+cluster+appNamespace+hash(ipAsInt+counter)
   *
   * @param namespace the appNamespace of the release
   * @return the unique release key
   */
  public static String generateReleaseKey(AppEnvClusterNamespace namespace) {
    return generate(namespace.getAppEnvCluster().getApp().getAppCode(), namespace.getAppEnvCluster().getName(), namespace.getAppNamespace().getName());
  }
}
