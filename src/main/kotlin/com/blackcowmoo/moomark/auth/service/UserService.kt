package com.blackcowmoo.moomark.auth.service

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
open class UserService {

  @Value("\${resources.user.default-picture}")
  private lateinit var defaultPicture: String

  @Autowired
  private lateinit var userRepository: UserRepository

  fun getUserById(authProvider: AuthProvider, id: String): User? {
    return userRepository.findByIdAndAuthProvider(id, authProvider)
  }

  fun signUp(id: String, authProvider: AuthProvider, nickname: String, email: String, picture: String): User {
    return userRepository.save(User(id, authProvider, email, nickname, picture, Role.USER))
  }

  fun withdraw(id: String, authProvider: AuthProvider) {
    userRepository.delete(userRepository.findByIdAndAuthProvider(id, authProvider))
  }

  fun withdraw(user: User) {
    userRepository.delete(user)
  }

  fun updateUser(user: User, nickname: String?, picture: String?): User {
    if (nickname != null && nickname.isNotEmpty()) {
      user.updateNickname(nickname)
    }

    if (picture != null && picture.isNotEmpty() && picture.startsWith("https://")) {
      user.updatePicture(picture)
    } else if (picture != null && picture.isEmpty()) {
      user.updatePicture(defaultPicture)
    }

    return userRepository.save(user)
  }
}
