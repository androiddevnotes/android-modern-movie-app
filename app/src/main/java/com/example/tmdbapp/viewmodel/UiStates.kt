package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.models.Movie
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

sealed class AIResponseState {
  data object Idle : AIResponseState()

  data object Loading : AIResponseState()

  data object Success : AIResponseState()

  data class Error(
    val message: String,
  ) : AIResponseState()
}

sealed class MovieDetailState {
  data object Loading : MovieDetailState()

  data class Success(
    val movie: Movie,
  ) : MovieDetailState()

  data class Error(
    val error: AppError,
    val movieId: Int,
  ) : MovieDetailState()
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
