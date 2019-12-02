package com.yofish.apollo.entity.bo;


import common.dto.ReleaseDTO;
import lombok.Data;

import java.util.Set;

@Data
public class ReleaseBO {

  private ReleaseDTO baseInfo;

  private Set<KVEntity> items;

}
