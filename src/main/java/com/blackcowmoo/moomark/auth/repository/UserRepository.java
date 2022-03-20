package com.blackcowmoo.moomark.auth.repository;

import com.blackcowmoo.moomark.auth.model.AuthProvider;
import com.blackcowmoo.moomark.auth.model.entity.User;
import com.blackcowmoo.moomark.auth.model.entity.UserId;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UserId> {
  User findByIdAndAuthProvider(String id, AuthProvider authProvider);
}
