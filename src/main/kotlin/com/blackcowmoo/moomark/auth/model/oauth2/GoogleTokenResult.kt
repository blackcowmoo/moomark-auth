package com.blackcowmoo.moomark.auth.model.oauth2

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleTokenResult(
  val iss: String? = null,
  val azp: String? = null,
  val aud: String? = null,
  val sub: String? = null,
  val email: String? = null,
  @JsonProperty("email_verified") val emailVerified: Boolean? = null,
  @JsonProperty("at_hash") val atHash: String? = null,
  val name: String? = null,
  val picture: String? = null,
  @JsonProperty("given_name") val givenName: String? = null,
  @JsonProperty("family_name") val familyName: String? = null,
  val locale: String? = null,
  val iat: Int? = null,
  val exp: Int? = null
)
