package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.AppError

fun handleNetworkError(
  errorMessage: String?,
  apiKeyManager: ApiKeyManager,
): AppError =
  when {
    apiKeyManager.getTmdbApiKey().isBlank() -> AppError.ApiKeyMissing
    errorMessage?.contains("UnknownHostException") == true -> AppError.NoInternet
    errorMessage?.contains("IOException") == true -> AppError.Network
    errorMessage?.contains("ResponseException") == true -> {
      if (errorMessage.contains("5")) {
        AppError.Server
      } else if (errorMessage.contains("401") || errorMessage.contains("Invalid API key")) {
        AppError.ApiKeyMissing
      } else {
        AppError.ApiError(errorMessage)
      }
    }

    else -> AppError.Unknown
  }
