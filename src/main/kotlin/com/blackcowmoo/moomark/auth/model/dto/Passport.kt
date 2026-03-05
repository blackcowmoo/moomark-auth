package com.blackcowmoo.moomark.auth.model.dto

import java.sql.Timestamp

import lombok.Data

@Data
class Passport(
    var exp: Timestamp? = null,
    var key: String? = null,
    var hash: String? = null
)