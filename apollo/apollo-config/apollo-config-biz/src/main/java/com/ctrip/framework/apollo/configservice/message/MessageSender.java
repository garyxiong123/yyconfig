package com.ctrip.framework.apollo.configservice.message;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface MessageSender {
  void sendMessage(String message, String channel);
}
