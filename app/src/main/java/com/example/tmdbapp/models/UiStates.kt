package com.example.tmdbapp.models

import com.example.tmdbapp.utils.AppError

sealed class ItemListUiState<out T> {
  data class Error(
    val error: AppError,
  ) : ItemListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : ItemListUiState<T>()

  data object Loading : ItemListUiState<Nothing>()
}

sealed class ItemDetailUiState<out T> {
  data object Loading : ItemDetailUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : ItemDetailUiState<T>()

  data class Error(
    val error: AppError,
    val itemId: Int,
  ) : ItemDetailUiState<Nothing>()
}

sealed class AiResponseUiState<out T> {
  data object Idle : AiResponseUiState<Nothing>()

  data object Loading : AiResponseUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : AiResponseUiState<T>()

  data class Error(
    val message: String,
  ) : AiResponseUiState<Nothing>()
}

sealed class ItemAuthUiState<out T> {
  data object Idle : ItemAuthUiState<Nothing>()

  data object Loading : ItemAuthUiState<Nothing>()

  data class RequestTokenCreated<T>(
    val data: T,
  ) : ItemAuthUiState<T>()

  data object Authenticated : ItemAuthUiState<Nothing>()

  data class Error(
    val message: String,
  ) : ItemAuthUiState<Nothing>()
}

sealed class ItemCreateListUiState<out T> {
  data object Idle : ItemCreateListUiState<Nothing>()

  data object Loading : ItemCreateListUiState<Nothing>()

  data class Success<T>(
    val data: T,
  ) : ItemCreateListUiState<T>()

  data class Error(
    val message: String,
  ) : ItemCreateListUiState<Nothing>()
}
