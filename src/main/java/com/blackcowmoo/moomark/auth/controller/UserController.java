package com.blackcowmoo.moomark.auth.controller;

import com.blackcowmoo.moomark.auth.model.entity.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

  private User getUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  @GetMapping
  public User getUserInfo() {
    return getUser();
  }
}
