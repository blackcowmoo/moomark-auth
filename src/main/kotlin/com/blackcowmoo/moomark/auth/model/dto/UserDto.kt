package com.blackcowmoo.moomark.auth.model.dto

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import lombok.Getter
import lombok.NoArgsConstructor

@Getter
@NoArgsConstructor
class UserDto(
  var id: String? = null,
  var email: String? = null,
  var name: String? = null,
  var picture: String? = null,
  var nickname: String? = null,
  var role: Role? = null,
  var provider: AuthProvider? = null
) {
  class Builder {
    private var id: String? = null
    private var email: String? = null
    private var name: String? = null
    private var picture: String? = null
    private var nickname: String? = null
    private var role: Role? = null
    private var provider: AuthProvider? = null

    fun id(id: String?): Builder = apply { this.id = id }
    fun email(email: String?): Builder = apply { this.email = email }
    fun name(name: String?): Builder = apply { this.name = name }
    fun picture(picture: String?): Builder = apply { this.picture = picture }
    fun nickname(nickname: String?): Builder = apply { this.nickname = nickname }
    fun role(role: Role?): Builder = apply { this.role = role }
    fun provider(provider: AuthProvider?): Builder = apply { this.provider = provider }

    fun build() = UserDto().apply {
      this.id = id
      this.email = email
      this.name = name
      this.picture = picture
      this.nickname = nickname
      this.role = role
      this.provider = provider
    }
  }

  companion object {
    @JvmStatic
    fun builder() = Builder()
  }
}
