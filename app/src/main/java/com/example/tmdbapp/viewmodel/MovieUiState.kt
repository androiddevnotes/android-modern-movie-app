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
