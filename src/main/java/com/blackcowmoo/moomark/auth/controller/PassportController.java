package com.blackcowmoo.moomark.auth.controller;

import javax.servlet.http.HttpServletResponse;

import com.blackcowmoo.moomark.auth.model.dto.PassportResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.PassportService;

import lombok.RequiredArgsConstructor;

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
  public User verifyPassport(
    @RequestHeader("x-moom-passport-user") String passport,
    @RequestHeader("x-moom-passport-key") String key
  ) {
    return passportService.parsePassport(passport, key);
  }

  @GetMapping
  public PassportResponse generatePassport(HttpServletResponse response) {
    PassportResponse passport = passportService.generatePassport(getUser());
    if (passport == null) {
      response.setStatus(401);
      return null;
    }

    return passport;
  }
}
