package com.blackcowmoo.moomark.auth.model.dto

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role
import lombok.Data

@Data
class TokenResponse(
  var id: String? = null,
  var provider: AuthProvider? = null,
  var role: Role? = null
)
