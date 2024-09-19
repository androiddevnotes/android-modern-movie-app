package com.example.tmdbapp.models

import kotlinx.serialization.*

@Serializable
data class MovieResponse(
  val page: Int,
  val results: List<Movie>,
  @SerialName("total_pages") val totalPages: Int,
  @SerialName("total_results") val totalResults: Int,
)
