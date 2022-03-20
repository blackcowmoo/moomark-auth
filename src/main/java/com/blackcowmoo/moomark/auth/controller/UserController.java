package com.blackcowmoo.moomark.auth.controller;

import com.blackcowmoo.moomark.auth.model.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
  public String getUserInfo() throws JsonProcessingException {
    User user = getUser();
    ObjectMapper mapper = new ObjectMapper();
    String userInfoString;
    userInfoString = mapper.writeValueAsString(user);

    return userInfoString;
  }
}
