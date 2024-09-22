package com.example.tmdbapp.network.responses.tmdb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateListResponse(
  @SerialName("status_message") val statusMessage: String,
  val success: Boolean,
  @SerialName("status_code") val statusCode: Int,
  @SerialName("list_id") val listId: Int,
)
