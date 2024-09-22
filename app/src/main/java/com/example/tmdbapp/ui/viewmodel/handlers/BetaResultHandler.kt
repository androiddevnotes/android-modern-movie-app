package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.BetaResponseUiState
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import com.example.tmdbapp.utils.Resource.Error
import com.example.tmdbapp.utils.Resource.Success
import kotlinx.coroutines.flow.MutableStateFlow

object BetaResultHandler {
  fun handleBetaResult(
    result: Resource<String>,
    betaResponseUiState: MutableStateFlow<BetaResponseUiState<String>>,
    apiKeyManager: ApiKeyManager,
  ) {
    when (result) {
      is Success -> {
        val response = result.data ?: ""
        betaResponseUiState.value = BetaResponseUiState.Success(response)
      }
      is Error -> {
        betaResponseUiState.value = BetaResponseUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
  }
}
