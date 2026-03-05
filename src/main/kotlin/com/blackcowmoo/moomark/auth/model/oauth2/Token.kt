package com.blackcowmoo.moomark.auth.model.oauth2

data class Token(
    val token: String,
    val refreshToken: String
)