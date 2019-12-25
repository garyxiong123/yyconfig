package common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReleaseDTO extends BaseDTO{
  private long id;

  private String releaseKey;

  private String name;

  private String appId;

  private String clusterName;

  private String namespaceName;

  private String configurations;

  private String comment;

  private boolean isAbandoned;


}
