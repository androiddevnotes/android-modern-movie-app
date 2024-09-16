package com.example.tmdbapp.models

import com.google.gson.annotations.SerializedName

data class Movie(
  val id: Int,
  @SerializedName("title") val title: String,
  @SerializedName("overview") val overview: String,
  @SerializedName("poster_path") val posterPath: String?,
  @SerializedName("vote_average") val voteAverage: Float,
  var isFavorite: Boolean = false,
)
