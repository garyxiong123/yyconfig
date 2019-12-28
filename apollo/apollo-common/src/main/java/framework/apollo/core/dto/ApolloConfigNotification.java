package framework.apollo.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Data
public class ApolloConfigNotification implements Serializable {
  private String namespaceName;
  private long notificationId;
  private volatile ApolloNotificationMessages messages;

  //for json converter
  public ApolloConfigNotification() {
  }

  public ApolloConfigNotification(String namespaceName, long notificationId) {
    this.namespaceName = namespaceName;
    this.notificationId = notificationId;
  }

  public void addMessage(String key, long notificationId) {
    if (this.messages == null) {
      synchronized (this) {
        if (this.messages == null) {
          this.messages = new ApolloNotificationMessages();
        }
      }
    }
    this.messages.put(key, notificationId);
  }

  @Override
  public String toString() {
    return "ApolloConfigNotification{" +
            "namespaceName='" + namespaceName + '\'' +
            ", notificationId=" + notificationId +
            '}';
  }
}
