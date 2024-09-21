package com.example.tmdbapp.utils

import com.example.tmdbapp.R

sealed class MovieError(
  open val messageResId: Int,
) {
  data class ApiError(
    val errorMessage: String,
    override val messageResId: Int = R.string.error_api,
  ) : MovieError(messageResId)

  data object Network : MovieError(R.string.error_network)

  data object Server : MovieError(R.string.error_server)

  data object NoInternet : MovieError(R.string.error_no_internet)

  data object ApiKeyMissing : MovieError(R.string.error_api_key_missing)

  data object Unknown : MovieError(R.string.error_unknown)
}
