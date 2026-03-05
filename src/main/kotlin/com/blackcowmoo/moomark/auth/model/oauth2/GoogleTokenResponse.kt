package com.blackcowmoo.moomark.auth.model.oauth2

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleTokenResponse(
    @JsonProperty("access_token") val accessToken: String? = null,
    @JsonProperty("expires_in") val expiresIn: Int? = null,
    val scope: String? = null,
    @JsonProperty("token_type") val tokenType: String? = null,
    @JsonProperty("id_token") val idToken: String? = null
) {
    fun isExpired(): Boolean {
        return expiresIn?.let { it <= 0 } ?: true
    }
}