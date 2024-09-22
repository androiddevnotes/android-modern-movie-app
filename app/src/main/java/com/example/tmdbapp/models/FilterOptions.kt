package com.example.tmdbapp.models

data class FilterOptions(
  val genres: List<Int> = emptyList(),
  val minRating: Float? = null,
  val releaseYear: Int? = null,
)
