package com.example.tmdbapp.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
  val id: Int,
  @SerialName("title") val title: String,
  @SerialName("overview") val overview: String,
  @SerialName("poster_path") val posterPath: String?,
  @SerialName("vote_average") val voteAverage: Float,
  @SerialName("release_date") val releaseDate: String?,
  var isFavorite: Boolean = false,
)
