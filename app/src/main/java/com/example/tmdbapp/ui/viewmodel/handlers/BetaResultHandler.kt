package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.BetaPieceUiState
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import timber.log.Timber

object BetaResultHandler {
  suspend fun handleBetaResult(
    result: Resource<String>,
    betaPieceUiState: MutableStateFlow<BetaPieceUiState<String>>,
    apiKeyManager: ApiKeyManager,
  ) {
    when (result) {
      is Resource.Success -> {
        result.data?.let { response ->
          betaPieceUiState.value = BetaPieceUiState.Success(response)
        } ?: run {
          val appError = AppError.ApiError("No data received")
          betaPieceUiState.value = BetaPieceUiState.Error(appError)
        }
      }
      is Resource.Error -> {
        Timber.e("Error fetching beta result: ${result.message}")
        val appError =
          when {
            apiKeyManager.tmdbApiKeyFlow.first().isBlank() -> AppError.ApiKeyMissing
            result.message == null -> {
              Timber.e("Error message is null")
              AppError.Unknown
            }
            else -> {
              Timber.e("Unhandled error: ${result.message}")
              AppError.ApiError(result.message)
            }
          }
        betaPieceUiState.value = BetaPieceUiState.Error(appError)
      }
    }
  }
}
