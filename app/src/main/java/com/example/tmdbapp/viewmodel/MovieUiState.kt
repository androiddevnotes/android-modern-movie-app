package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.AppError

sealed class UiState<out T> {
  data class Error(
    val error: AppError,
  ) : UiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : UiState<T>()

  data object Loading : UiState<Nothing>()
}
