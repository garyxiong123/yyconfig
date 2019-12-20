package com.yofish.apollo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Data
public class InstanceConfigDTO {
  private ReleaseDTO release;
  private LocalDateTime releaseDeliveryTime;
  private LocalDateTime dataChangeLastModifiedTime;


}
