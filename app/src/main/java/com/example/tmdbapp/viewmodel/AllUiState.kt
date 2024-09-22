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

sealed class MovieDetailState<out T> {
  data object Loading : MovieDetailState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : MovieDetailState<T>()

  data class Error(
    val error: AppError,
    val movieId: Int,
  ) : MovieDetailState<Nothing>()
}

sealed class AIResponseState<out T> {
  data object Idle : AIResponseState<Nothing>()

  data object Loading : AIResponseState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AIResponseState<T>()

  data class Error(
    val message: String,
  ) : AIResponseState<Nothing>()
}

sealed class AuthState {
  data object Idle : AuthState()

  data object Loading : AuthState()

  data class RequestTokenCreated(
    val token: String,
  ) : AuthState()

  data object Authenticated : AuthState()

  data class Error(
    val message: String,
  ) : AuthState()
}

sealed class CreateListState {
  data object Idle : CreateListState()

  data object Loading : CreateListState()

  data class Success(
    val listId: Int,
  ) : CreateListState()

  data class Error(
    val message: String,
  ) : CreateListState()
}
