package com.example.tmdbapp.network.responses.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
  @SerialName("request_token") val requestToken: String,
)
