package com.example.tmdbapp.network.responses.tmdb

import kotlinx.serialization.Serializable

@Serializable
data class CreateListRequest(
  val name: String,
  val description: String,
  val language: String = "en",
)
