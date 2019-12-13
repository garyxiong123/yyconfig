package com.yofish.apollo.message;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public interface ReleaseMessageSender {
  void sendMessage(String message, String channel);
}
