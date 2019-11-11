package jpa.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/10 上午8:48
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class DeployCommandConfigVO {

    private Long deployConfigId;

    private String branch;

    private String dockerImageTag;
}
