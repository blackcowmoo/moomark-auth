package com.blackcowmoo.moomark.auth.model.dto

import com.blackcowmoo.moomark.auth.model.AuthProvider
import com.blackcowmoo.moomark.auth.model.Role

@lombok.Builder
@lombok.Getter
@lombok.NoArgsConstructor
class UserDto(
    var id: String? = null,
    var email: String? = null,
    var name: String? = null,
    var picture: String? = null,
    var nickname: String? = null,
    var role: Role? = null,
    var provider: AuthProvider? = null
)