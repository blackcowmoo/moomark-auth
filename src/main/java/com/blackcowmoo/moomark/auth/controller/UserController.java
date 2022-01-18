package com.blackcowmoo.moomark.auth.controller;

import javax.servlet.http.HttpSession;
import com.blackcowmoo.moomark.auth.exception.JpaException;
import com.blackcowmoo.moomark.auth.model.dto.SessionUser;
import com.blackcowmoo.moomark.auth.model.dto.UserDto;
import com.blackcowmoo.moomark.auth.service.UserService;
import com.blackcowmoo.moomark.auth.service.UserServiceFactory;
import com.blackcowmoo.moomark.auth.service.UserServiceType;
import com.blackcowmoo.moomark.auth.util.ModelMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  @Autowired
  private final UserServiceFactory serviceFactory;
  private final HttpSession httpSession;

  private SessionUser getSessionUser() {
    return (SessionUser) httpSession.getAttribute("user");
  }

  private long getSessionUserId() {
    return getSessionUser().getId();
  }

  @GetMapping
  public UserDto getUserInfo(@RequestBody UserServiceType type) {
    return ModelMapperUtils.getModelMapper()
        .map(serviceFactory.getUserService(type).getUserById(getSessionUserId()), UserDto.class);
  }

  @PutMapping
  public ResponseEntity<Boolean> modUserInfo(@RequestBody String nickname,
      @RequestParam UserServiceType type) {
    boolean flag = false;
    try {
      flag = serviceFactory.getUserService(type).updateUserNickname(getSessionUserId(), nickname);
    } catch (JpaException e) {
      e.printStackTrace();
    }
    return ResponseEntity.ok(flag);
  }

  @GetMapping(value = "/env")
  public String getProfile() {
    return serviceFactory.getUserService(UserServiceType.NORMAL).getEnv();
  }
}
