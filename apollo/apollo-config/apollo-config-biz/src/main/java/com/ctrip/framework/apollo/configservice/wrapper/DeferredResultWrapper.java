package com.ctrip.framework.apollo.configservice.wrapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import framework.apollo.core.dto.ApolloConfigNotification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DeferredResultWrapper {
  private static final long TIMEOUT = 60 * 1000;
  private static final ResponseEntity<List<ApolloConfigNotification>> NOT_MODIFIED_RESPONSE_LIST = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

  private Map<String, String> normalizedNamespaceName2OriginalNamespaceNameMap;
  private DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> deferredResult;


  public DeferredResultWrapper() {
    deferredResult = new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE_LIST);
  }

  public void fillNormalizedNamespaceName2OriginalNamespaceNameMap(String originalNamespaceName, String normalizedNamespaceName) {
    if (normalizedNamespaceName2OriginalNamespaceNameMap == null) {
      normalizedNamespaceName2OriginalNamespaceNameMap = Maps.newHashMap();
    }
    normalizedNamespaceName2OriginalNamespaceNameMap.put(normalizedNamespaceName, originalNamespaceName);
  }


  public void onTimeout(Runnable timeoutCallback) {
    deferredResult.onTimeout(timeoutCallback);
  }

  public void onCompletion(Runnable completionCallback) {
    deferredResult.onCompletion(completionCallback);
  }


  public void setDeferredResult(ApolloConfigNotification notification) {
    setResult(Lists.newArrayList(notification));
  }

  /**
   * The appNamespace name is used as a key in client side, so we have to return the original one instead of the correct one
   */
  public void setResult(List<ApolloConfigNotification> notifications) {
    if (normalizedNamespaceName2OriginalNamespaceNameMap != null) {
      notifications.stream().filter(notification -> normalizedNamespaceName2OriginalNamespaceNameMap.containsKey
          (notification.getNamespaceName())).forEach(notification -> notification.setNamespaceName(
              normalizedNamespaceName2OriginalNamespaceNameMap.get(notification.getNamespaceName())));
    }

    deferredResult.setResult(new ResponseEntity<>(notifications, HttpStatus.OK));
  }

  public DeferredResult<ResponseEntity<List<ApolloConfigNotification>>> getDeferredResult() {
    return deferredResult;
  }
}
