package com.blackcowmoo.moomark.auth.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class UserController {
  @GetMapping("/test")
  public String index() {
    return "Hello world";
  }
}
