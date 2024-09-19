package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.models.*
import com.example.tmdbapp.utils.*

sealed class MovieUiState {
  data class Error(
    val error: MovieError,
  ) : MovieUiState()

  data class Success(
    val movies: List<Movie>,
  ) : MovieUiState()

  object Loading : MovieUiState()
}
