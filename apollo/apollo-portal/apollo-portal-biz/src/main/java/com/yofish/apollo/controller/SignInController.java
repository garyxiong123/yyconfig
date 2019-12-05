package com.yofish.apollo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author lepdou 2017-08-30
 */
@Controller
public class SignInController {

/*  @RequestMapping(value = "/signin", method = RequestMethod.GET)
  public String login(@RequestParam(value = "error", required = false) String error,
                      @RequestParam(value = "logout", required = false) String logout) {
    return "login.html";
  }*/


   @RequestMapping(value = "/signin", method = RequestMethod.POST)
  public String loginByPost() {
    return "true";
  }

}
