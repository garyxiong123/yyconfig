package common.dto;

import lombok.Data;

@Data
public class ClusterDTO extends BaseDTO{

  private long id;

  private String name;

  private String appId;
}
