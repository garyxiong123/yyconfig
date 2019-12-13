package com.yofish.apollo.message;


import com.yofish.apollo.domain.ReleaseMessage;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ReleaseMessageListener {
  void handleReleaseMessage(ReleaseMessage message, String channel);
}
