package com.example.tmdbapp.utils

import com.example.tmdbapp.R

sealed class AppError(
  open val messageResId: Int,
  open val userFriendlyMessage: String,
) {
  data class ApiError(
    val errorMessage: String,
    override val messageResId: Int = R.string.error_api,
    override val userFriendlyMessage: String = "An API error occurred: $errorMessage",
  ) : AppError(messageResId, userFriendlyMessage)

  data object ApiKeyMissing : AppError(R.string.error_api_key_missing, "API key is missing")

  data object Unknown : AppError(R.string.error_unknown, "An unknown error occurred")
}
