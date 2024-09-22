package com.example.tmdbapp.network.responses.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTokenResponse(
  val success: Boolean,
  @SerialName("expires_at") val expiresAt: String,
  @SerialName("request_token") val requestToken: String,
)
