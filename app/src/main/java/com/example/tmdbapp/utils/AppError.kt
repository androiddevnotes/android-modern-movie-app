package com.example.tmdbapp.utils

import com.example.tmdbapp.R

sealed class AppError(
  open val messageResId: Int,
) {
  data class ApiError(
    val errorMessage: String,
    override val messageResId: Int = R.string.error_api,
  ) : AppError(messageResId)

  data object Network : AppError(R.string.error_network)

  data object Server : AppError(R.string.error_server)

  data object NoInternet : AppError(R.string.error_no_internet)

  data object ApiKeyMissing : AppError(R.string.error_api_key_missing)

  data object Unknown : AppError(R.string.error_unknown)
}
