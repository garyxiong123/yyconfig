package jpa.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/9 下午6:41
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class DeployCommandDetailVO {

    private String remark;

    private String logDir;
}
