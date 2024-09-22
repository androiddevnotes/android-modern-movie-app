package com.example.tmdbapp.models

import com.example.tmdbapp.utils.AppError

sealed class ListUiState<out T> {
  data class Error(
    val error: AppError,
  ) : ListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : ListUiState<T>()

  data object Loading : ListUiState<Nothing>()
}

sealed class DetailUiState<out T> {
  data object Loading : DetailUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : DetailUiState<T>()

  data class Error(
    val error: AppError,
    val itemId: Int,
  ) : DetailUiState<Nothing>()
}

sealed class AIResponseUiState<out T> {
  data object Idle : AIResponseUiState<Nothing>()

  data object Loading : AIResponseUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AIResponseUiState<T>()

  data class Error(
    val message: String,
  ) : AIResponseUiState<Nothing>()
}

sealed class AuthUiState<out T> {
  data object Idle : AuthUiState<Nothing>()

  data object Loading : AuthUiState<Nothing>()

  data class RequestTokenCreated<T>(
    val data: T,
  ) : AuthUiState<T>()

  data object Authenticated : AuthUiState<Nothing>()

  data class Error(
    val message: String,
  ) : AuthUiState<Nothing>()
}

sealed class CreateListUiState<out T> {
  data object Idle : CreateListUiState<Nothing>()

  data object Loading : CreateListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : CreateListUiState<T>()

  data class Error(
    val message: String,
  ) : CreateListUiState<Nothing>()
}
