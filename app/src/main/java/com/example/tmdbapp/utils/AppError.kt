package com.example.tmdbapp.utils

import com.example.tmdbapp.R

sealed class AppError(
  open val messageResId: Int,
) {
  data class ApiError(
    val errorMessage: String,
    override val messageResId: Int = R.string.error_api,
  ) : AppError(messageResId)

  data object ApiKeyMissing : AppError(R.string.error_api_key_missing)

  data object Unknown : AppError(R.string.error_unknown)
}

