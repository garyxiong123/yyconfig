package com.yofish.apollo.controller.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Since sso auth information has a limited expiry time, so we need to do sso heartbeat to keep the
 * information refreshed when unavailable
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Controller
@RequestMapping("/sso_heartbeat")
public class SsoHeartbeatController {
  /*@Autowired
  private SsoHeartbeatHandler handler;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public void heartbeat(HttpServletRequest request, HttpServletResponse response) {
    handler.doHeartbeat(request, response);
  }*/
}
