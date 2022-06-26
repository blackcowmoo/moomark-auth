package com.blackcowmoo.moomark.auth.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.PassportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/passport")
public class PassportController {
  private final PassportService passportService;

  private User getUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  @GetMapping("/verify/public")
  public String getMyInfo() {
    return passportService.getPublicKeyString();
  }

  @GetMapping("/verify")
  public User verifyPassport(@RequestParam String passport) {
    return passportService.parsePassport(passport);
  }

  @GetMapping
  public String generatePassport() {
    return passportService.generatePassport(getUser());
  }
}
