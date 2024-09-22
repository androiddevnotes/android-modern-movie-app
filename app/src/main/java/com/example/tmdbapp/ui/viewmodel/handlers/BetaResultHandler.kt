package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.BetaResponseUiState
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow

object BetaResultHandler {
  suspend fun handleBetaResult(
    result: Resource<String>,
    betaResponseUiState: MutableStateFlow<BetaResponseUiState<String>>,
    apiKeyManager: ApiKeyManager,
  ) {
    when (result) {
      is Resource.Success -> {
        result.data?.let { response ->
          betaResponseUiState.value = BetaResponseUiState.Success(response)
        } ?: run {
          betaResponseUiState.value = BetaResponseUiState.Error(handleNetworkError("No data received", apiKeyManager))
        }
      }
      is Resource.Error -> {
        betaResponseUiState.value = BetaResponseUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
  }
}
