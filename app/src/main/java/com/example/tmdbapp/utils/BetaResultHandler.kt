package com.example.tmdbapp.utils

import com.example.tmdbapp.models.BetaResponseUiState
import com.example.tmdbapp.network.handleNetworkError
import kotlinx.coroutines.flow.MutableStateFlow

object BetaResultHandler {
  fun handleBetaResult(
    result: Resource<String>,
    betaResponseUiState: MutableStateFlow<BetaResponseUiState<String>>,
    apiKeyManager: ApiKeyManager,
  ) {
    when (result) {
      is Resource.Success -> {
        val response = result.data ?: ""
        betaResponseUiState.value = BetaResponseUiState.Success(response)
      }
      is Resource.Error -> {
        betaResponseUiState.value = BetaResponseUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
  }
}
