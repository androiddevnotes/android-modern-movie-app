package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.MovieError

fun handleError(
  errorMessage: String?,
  apiKeyManager: ApiKeyManager,
): MovieError =
  when {
    apiKeyManager.getTmdbApiKey().isBlank() -> MovieError.ApiKeyMissing
    errorMessage?.contains("UnknownHostException") == true -> MovieError.NoInternet
    errorMessage?.contains("IOException") == true -> MovieError.Network
    errorMessage?.contains("ResponseException") == true -> {
      if (errorMessage.contains("5")) {
        MovieError.Server
      } else if (errorMessage.contains("401") || errorMessage.contains("Invalid API key")) {
        MovieError.ApiKeyMissing
      } else {
        MovieError.ApiError(errorMessage)
      }
    }

    else -> MovieError.Unknown
  }
