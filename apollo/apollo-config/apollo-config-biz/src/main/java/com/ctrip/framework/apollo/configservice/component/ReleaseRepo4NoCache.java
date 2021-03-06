package com.ctrip.framework.apollo.configservice.component;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/4/15 上午10:59
 */
public class ReleaseRepo4NoCache implements ReleaseRepo {


    @Autowired
    private ReleaseService releaseService;

    @Override
    public Release findActiveOne(long id, LongNamespaceVersion clientMessages) {
        return releaseService.findActiveOne(id);
    }

    @Override
    public Release findLatestActiveRelease(String configAppId, String configClusterName, String env, String configNamespace,
                                           LongNamespaceVersion clientMessages) {
        return releaseService.findLatestActiveRelease(configAppId, configClusterName, env, configNamespace);
    }

    @Override
    public void onReceiveReleaseMessage(ReleaseMessage message, String channel) {
        // since there is no cache, so do nothing
    }


}
