package com.ctrip.framework.apollo.configservice.pattern.strategy;

import com.ctrip.framework.apollo.configservice.domain.ConfigClient4Version;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yofish.apollo.domain.ReleaseMessage;
import com.yofish.apollo.service.ReleaseMessageService;
import com.yofish.yyconfig.common.framework.apollo.core.ConfigConsts;
import com.yofish.yyconfig.common.framework.apollo.core.dto.LongNamespaceVersion;
import com.yofish.yyconfig.common.framework.apollo.core.dto.NamespaceVersion;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass4Context;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明:版本比价算法
 * @Date: 2020/8/27 上午11:23
 */
@Component
public class VersionCompareStrategy {

    /**
     * 计算新的namespaceVersion：
     * <p>
     * 1： 是否是新的 ns
     * 2： 新的计算，并且加入
     *
     * @param configClient4Version
     * @return
     */
    public List<NamespaceVersion> calcNewNsVersions(ConfigClient4Version configClient4Version) {

        List<NamespaceVersion> newNsVersionsRsp = Lists.newArrayList();
        for (NamespaceVersion namespaceVersion : configClient4Version.getClientNsVersions()) {
            if (configClient4Version.isNewVersion(namespaceVersion)) {
                NamespaceVersion newNsVersion = buildNewNsVersion(namespaceVersion, configClient4Version);
                newNsVersionsRsp.add(newNsVersion);
            }
        }
        return newNsVersionsRsp;
    }


    private NamespaceVersion buildNewNsVersion(NamespaceVersion namespaceVersion4Client, ConfigClient4Version configClient4Version) {
        String namespace = namespaceVersion4Client.getNamespaceName();
        long latestId = configClient4Version.getLatestReleaseMsgId(namespace);

        NamespaceVersion namespaceVersion = new NamespaceVersion(namespace, latestId);
        LongNamespaceVersion longNamespaceVersion = new LongNamespaceVersion(configClient4Version.getNewMap(namespaceVersion4Client.getNamespaceName()));
        namespaceVersion.setLongNamespaceVersion(longNamespaceVersion);
        return namespaceVersion;
    }


}
