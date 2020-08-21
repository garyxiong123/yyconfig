package com.yofish.apollo.model.dto.Req;

import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import com.yofish.yyconfig.common.common.utils.InputValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 创建集群请求
 * @Date: 2020/7/27 下午4:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateClusterReqDTO {

    Long appId;

    String clusterName;

    String envs;

    String env;

    public void paramCheck() {
        if (!InputValidator.isValidClusterNamespace(clusterName)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("Cluster格式错误: %s", InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
        }
    }
}
