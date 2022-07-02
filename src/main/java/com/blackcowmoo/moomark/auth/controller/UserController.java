package com.blackcowmoo.moomark.auth.controller;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.service.UserService;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {
  private static class ModifyUserBody {
    public String nickname;
    public String picture;
  }

  @Value("${resources.user.default-picture}")
  private final String defaultPicture;

  private final UserService userService;

  private User getUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  @GetMapping
  public User getMyInfo() {
    return getUser();
  }

  @GetMapping("/{provider}/{userId}")
  public User getUserInfo(@PathVariable("provider") String provider, @PathVariable("userId") String userId,
      HttpServletResponse response) {
    User user = userService.getUserById(AuthProvider.getAuthProviderValue(provider), userId);
    if (user != null) {
      return user;
    }

    response.setStatus(404);
    return null;
  }

  @DeleteMapping
  public void deleteUser() {
    userService.withdraw(getUser());
  }

  @PutMapping
  public User modifyUser(@RequestBody ModifyUserBody body) {
    User user = getMyInfo();
    String nickname = body.nickname;
    String picture = body.picture;

    if (body.nickname.equals("") || body.nickname == null) {
      nickname = user.getNickname();
    }
    if (body.picture.equals("") || body.picture == null) {
      picture = defaultPicture;
    }

    return userService.updateUser(user, nickname, picture);
  }
}
