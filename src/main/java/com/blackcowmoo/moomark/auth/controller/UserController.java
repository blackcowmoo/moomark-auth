package com.blackcowmoo.moomark.auth.controller;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.UserService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {
  private final UserService userService;

  private User getUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  @GetMapping
  public User getMyInfo() {
    return getUser();
  }

  @GetMapping("/{provider}/{userId}")
  public User getUserInfo(@PathVariable("provider") String provider, @PathVariable("userId") String userId) {
    return userService.getUserById(AuthProvider.getAuthProviderValue(provider), userId);
  }

  @DeleteMapping
  public void deleteUser() {
    userService.withdraw(getUser());
  }
}
