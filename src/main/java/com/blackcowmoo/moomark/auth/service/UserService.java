package com.blackcowmoo.moomark.auth.service;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.Role;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired
  UserRepository userRepository;

  public User getUserById(String id, AuthProvider authProvider) {
    return userRepository.findByIdAndAuthProvider(id, authProvider);
  }

  public User signUp(String id, AuthProvider authProvider, String name, String email, String picture) {
    return userRepository.save(new User(id, authProvider, name, email, name, picture, Role.USER));
  }

  public void withdraw(String id, AuthProvider authProvider) {
    userRepository.delete(userRepository.findByIdAndAuthProvider(id, authProvider));
  }

  public void withdraw(User user) {
    userRepository.delete(user);
  }

  // @Override
  // public void updateUser(User user) {
  // userRepository.save(user);
  // }

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

  // @Override
  // public Optional<User> login(String email, String password, AuthProvider
  // authProvider) {
  // return userRepository.findByEmailAndAuthProvider(email, authProvider);
  // }

  // public User loginOrSignUp(User user) {
  // Optional<User> newOrExistUser = login(user.getEmail(), null,
  // user.getAuthProvider());
  // return newOrExistUser.orElse(signUp(user));
  // }

}
