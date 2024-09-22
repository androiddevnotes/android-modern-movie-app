package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.BetaAiUiState
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

object BetaResultHandler {
  suspend fun handleBetaResult(
    result: Resource<String>,
    betaAiUiState: MutableStateFlow<BetaAiUiState<String>>,
    apiKeyManager: ApiKeyManager,
  ) {
    when (result) {
      is Resource.Success -> {
        result.data?.let { response ->
          betaAiUiState.value = BetaAiUiState.Success(response)
        } ?: run {
          betaAiUiState.value = BetaAiUiState.Error(handleNetworkError("No data received", apiKeyManager))
        }
      }
      is Resource.Error -> {
        Timber.e("Error fetching beta result: ${result.message}")
        betaAiUiState.value = BetaAiUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
  }
}
