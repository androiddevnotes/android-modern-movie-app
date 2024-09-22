package com.example.tmdbapp.network.responses.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionResponse(
  val success: Boolean,
  @SerialName("session_id") val sessionId: String,
)
