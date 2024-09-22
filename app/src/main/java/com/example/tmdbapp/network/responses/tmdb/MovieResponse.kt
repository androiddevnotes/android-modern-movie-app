package com.example.tmdbapp.network.responses.tmdb

import com.example.tmdbapp.models.Movie
import kotlinx.serialization.*

@Serializable
data class MovieResponse(
  val page: Int,
  val results: List<Movie>,
  @SerialName("total_pages") val totalPages: Int,
  @SerialName("total_results") val totalResults: Int,
)
