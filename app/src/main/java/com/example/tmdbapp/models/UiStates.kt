package com.example.tmdbapp.models

import com.example.tmdbapp.utils.AppError

sealed class AlphaListUiState<out T> {
  data class Error(
    val error: AppError,
  ) : AlphaListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AlphaListUiState<T>()

  data object Loading : AlphaListUiState<Nothing>()
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

sealed class AlphaCreateListUiState<out T> {
  data object Idle : AlphaCreateListUiState<Nothing>()

  data object Loading : AlphaCreateListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AlphaCreateListUiState<T>()

  data class Error(
    val error: AppError,
  ) : AlphaCreateListUiState<Nothing>()
}
