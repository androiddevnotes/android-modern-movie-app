package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.MovieError

sealed class UiState<out T> {
  data class Error(
    val error: MovieError,
  ) : UiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : UiState<T>()

  data object Loading : UiState<Nothing>()
}

// Type alias for MovieUiState to maintain backwards compatibility
typealias MovieUiState = UiState<List<com.example.tmdbapp.models.Movie>>
