package com.example.tmdbapp.models

import com.example.tmdbapp.utils.AppError

sealed class TmdbListUiState<out T> {
  data class Error(
    val error: AppError,
  ) : TmdbListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : TmdbListUiState<T>()

  data object Loading : TmdbListUiState<Nothing>()
}

sealed class TmdbDetailUiState<out T> {
  data object Loading : TmdbDetailUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : TmdbDetailUiState<T>()

  data class Error(
    val error: AppError,
    val itemId: Int,
  ) : TmdbDetailUiState<Nothing>()
}

sealed class BetaPieceUiState<out T> {
  data object Idle : BetaPieceUiState<Nothing>()

  data object Loading : BetaPieceUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : BetaPieceUiState<T>()

  data class Error(
    val error: AppError,
  ) : BetaPieceUiState<Nothing>()
}

sealed class TmdbAuthUiState<out T> {
  data object Idle : TmdbAuthUiState<Nothing>()

  data object Loading : TmdbAuthUiState<Nothing>()

  data class RequestTokenCreated(
    val data: String,
  ) : TmdbAuthUiState<String>()

  data object Authenticated : TmdbAuthUiState<Nothing>()

  data class Error(
    val error: AppError,
  ) : TmdbAuthUiState<Nothing>()
}

sealed class TmdbCreateListUiState<out T> {
  data object Idle : TmdbCreateListUiState<Nothing>()

  data object Loading : TmdbCreateListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : TmdbCreateListUiState<T>()

  data class Error(
    val error: AppError,
  ) : TmdbCreateListUiState<Nothing>()
}
