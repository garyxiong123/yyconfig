package common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/31 下午10:36
 */
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class NamespaceBo {

    protected String appCode;
    protected String env;
    protected String clusterName;
    protected String dataCenter;
    protected String namespaceName;

}
