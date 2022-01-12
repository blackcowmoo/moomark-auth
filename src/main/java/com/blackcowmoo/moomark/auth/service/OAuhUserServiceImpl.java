package com.blackcowmoo.moomark.auth.service;

import java.util.Optional;
import com.blackcowmoo.moomark.auth.exception.JpaErrorCode;
import com.blackcowmoo.moomark.auth.exception.JpaException;
import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Profile("production")
public class OAuhUserServiceImpl implements UserService {
  @Autowired
  UserRepository userRepository;

  @Override
  public User getUserById(long userId) {
    return userRepository.getOne(userId);
  }

  @Override
  public void updateUser(User user) {
    userRepository.save(user);
  }

  @Override
  public boolean updateUserNickname(long userId, String nickname) {
    try {
      User willUpdateUser = userRepository.getOne(userId);
      willUpdateUser.updateNickname(nickname);
      updateUser(willUpdateUser);
    } catch (Exception e) {
      log.error(e);
      return false;
    }
    return true;
  }

  @Override
  public User signUp(User user) throws Exception {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new JpaException(JpaErrorCode.ALREADY_EXIST_EMAIL.getMsg(),
          JpaErrorCode.ALREADY_EXIST_EMAIL.getCode());
    }
    
    return userRepository.save(user);
  }

  @Override
  public Optional<User> login(String email, String password, AuthProvider authProvider) {
    return userRepository.findByEmailAndAuthProvider(email, authProvider);
  }

  public User loginOrSignUp(User user) throws Exception {
    Optional<User> newOrExistUser = login(user.getEmail(), null, user.getAuthProvider());
    return newOrExistUser.orElse(signUp(user));
  }

  @Override
  public User findByName(String name) throws JpaException {
    return userRepository.findByName(name).orElseThrow(
        () -> new JpaException("Can not find user by name"));
  }

  @Override
  public User findByEmail(String email) throws JpaException {
    return userRepository.findByEmail(email).orElseThrow(
        () -> new JpaException("Can not find user by Email"));
  }

}
