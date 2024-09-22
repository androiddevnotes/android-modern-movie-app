package com.example.tmdbapp.network.responses.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbErrorResponse(
  @SerialName("status_message") val statusMessage: String,
  @SerialName("status_code") val statusCode: Int,
)
