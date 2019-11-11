package jpa.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/10 上午9:07
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class DeployFlowMarathonVO {

    private String marathonServiceId;

    private String marathonServiceConfig;

    private String marathonDeploymentId;

    private String marathonDeploymentVersion;
}
