package com.yofish.apollo.message;

import com.yofish.apollo.domain.ReleaseMessage;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/12 下午1:43
 */
public interface ReleaseMessagePublisher {


    void addMessageListener(ReleaseMessageListener listener);


    void fireMessageScanned(List<ReleaseMessage> messages);

}
