package com.blackcowmoo.moomark.auth.service;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Value("${resources.user.default-picture}")
  private String defaultPicture;

  @Autowired
  UserRepository userRepository;

  public User getUserById(AuthProvider authProvider, String id) {
    return userRepository.findByIdAndAuthProvider(id, authProvider);
  }

  public User signUp(String id, AuthProvider authProvider, String nickname, String email, String picture) {
    return userRepository.save(new User(id, authProvider, email, nickname, picture, Role.USER));
  }

  public void withdraw(String id, AuthProvider authProvider) {
    userRepository.delete(userRepository.findByIdAndAuthProvider(id, authProvider));
  }

  public void withdraw(User user) {
    userRepository.delete(user);
  }

  public User updateUser(User user, String nickname, String picture) {
    if (nickname != null && !nickname.equals("")) {
      user.updateNickname(nickname);
    }

    if (picture != null && !picture.equals("")) {
      user.updatePicture(picture);
    } else if (picture.equals("")) {
      user.updatePicture(defaultPicture);
    }

    return userRepository.save(user);
  }

  // @Override
  // public boolean updateUserNickname(long userId, String nickname) {
  // try {
  // User willUpdateUser = userRepository.getOne(userId);
  // willUpdateUser.updateNickname(nickname);
  // updateUser(willUpdateUser);
  // } catch (Exception e) {
  // log.error(e);
  // return false;
  // }
  // return true;
  // }

}
