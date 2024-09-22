package com.example.tmdbapp.network

import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.AppError
import kotlinx.coroutines.flow.first
import timber.log.Timber

suspend fun handleNetworkError(
  errorMessage: String?,
  apiKeyManager: ApiKeyManager,
): AppError =
  when {
    apiKeyManager.tmdbApiKeyFlow.first().isBlank() -> {
      AppError.ApiKeyMissing
    }
    errorMessage == null -> {
      Timber.e("Error message is null")
      AppError.Unknown
    }
    else -> {
      Timber.e("Unhandled error: $errorMessage")
      AppError.ApiError(errorMessage)
    }
  }
