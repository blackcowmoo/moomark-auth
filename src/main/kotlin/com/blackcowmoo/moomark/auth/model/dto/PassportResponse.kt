package com.blackcowmoo.moomark.auth.model.dto

import lombok.Data

@Data
class PassportResponse(
  var key: String? = null,
  var passport: String? = null
)
