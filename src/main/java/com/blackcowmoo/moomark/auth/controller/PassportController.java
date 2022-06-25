package com.blackcowmoo.moomark.auth.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.PassportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/passport")
public class PassportController {
  private final PassportService passportService;

  private User getUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  @GetMapping("/verify/public")
  public String getMyInfo() {
    return passportService.getPublicKey();
  }

  @GetMapping("/verify")
  public User verifyPassport(@RequestParam String passport) {
    log.info(passport);
    return getUser();
  }

  @GetMapping
  public String generatePassport() {
    User user = getUser();

    return "user";
  }
}
