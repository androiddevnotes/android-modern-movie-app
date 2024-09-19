package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.utils.MovieError

sealed class MovieUiState {
  data class Error(
    val error: MovieError,
  ) : MovieUiState()

  data class Success(
    val movies: List<Movie>,
  ) : MovieUiState()

  object Loading : MovieUiState()
}
