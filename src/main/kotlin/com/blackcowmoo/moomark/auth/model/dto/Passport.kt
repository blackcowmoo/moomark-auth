package com.blackcowmoo.moomark.auth.model.dto

import lombok.Data
import java.sql.Timestamp

@Data
class Passport(
  var exp: Timestamp? = null,
  var key: String? = null,
  var hash: String? = null
)
