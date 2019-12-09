package com.yofish.apollo.controller;


//import com.yofish.apollo.model.vo.Organization;
import com.yofish.apollo.service.PortalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@RestController
@RequestMapping("/organizations")
public class OrganizationController {
  @Autowired
  private PortalConfig portalConfig;

//
//  @RequestMapping
//  public List<Organization> loadOrganization() {
//    return portalConfig.organizations();
//  }
}
