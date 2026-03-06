package com.blackcowmoo.moomark.auth.controller

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.entity.User
import com.blackcowmoo.moomark.auth.service.UserService
import lombok.RequiredArgsConstructor
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
class UserController(
  private val userService: UserService
) {
  class ModifyUserBody {
    var nickname: String? = null
    var picture: String? = null
  }

  private fun getUser(): User {
    return SecurityContextHolder.getContext().authentication.principal as User
  }

  @GetMapping
  fun getMyInfo(): User {
    return getUser()
  }

  @GetMapping("/{provider}/{userId}")
  fun getUserInfo(
    @PathVariable("provider") provider: String,
    @PathVariable("userId") userId: String,
    response: HttpServletResponse
  ): User? {
    val user = userService.getUserById(AuthProvider.getAuthProviderValue(provider), userId)
    if (user != null) {
      return user
    }
    response.status = 404
    return null
  }

  @DeleteMapping
  fun deleteUser() {
    userService.withdraw(getUser())
  }

  @PutMapping
  fun modifyUser(@RequestBody body: ModifyUserBody): User {
    val user = getMyInfo()
    return userService.updateUser(user, body.nickname, body.picture)
  }
}
