package com.ctrip.framework.apollo.configservice.message;


import com.yofish.apollo.domain.ReleaseMessage;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ReleaseMessageListener {
  void handleMessage(ReleaseMessage message, String channel);
}
