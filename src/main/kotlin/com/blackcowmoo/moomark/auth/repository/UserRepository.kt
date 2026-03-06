package com.blackcowmoo.moomark.auth.repository

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.model.entity.UserId
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, UserId> {
  fun findByIdAndAuthProvider(id: String, authProvider: AuthProvider): User?
}
