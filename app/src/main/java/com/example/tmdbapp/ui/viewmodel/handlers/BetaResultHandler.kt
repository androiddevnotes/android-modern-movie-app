package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.BetaPieceUiState
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
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
          betaPieceUiState.value = BetaPieceUiState.Error(handleNetworkError("No data received", apiKeyManager))
        }
      }
      is Resource.Error -> {
        Timber.e("Error fetching beta result: ${result.message}")
        betaPieceUiState.value = BetaPieceUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
  }
}
