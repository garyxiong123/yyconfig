package com.ctrip.framework.apollo.configservice.component;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.pattern.listener.releasemessage.ReleaseMessageListener;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/4/15 上午10:58
 */
public interface ReleaseRepo extends ReleaseMessageListener {


    /**
     * Find active release by id
     */
    Release findActiveOne(long id, LongNamespaceVersion clientMessages);

    /**
     * Find active release by app id, cluster name and appNamespace name
     */
    Release findLatestActiveRelease(String configAppId, String configClusterName, String env,
                                    String configNamespaceName, LongNamespaceVersion clientMessages);

}
