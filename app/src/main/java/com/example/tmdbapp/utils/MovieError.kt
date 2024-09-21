package com.example.tmdbapp.utils

import com.example.tmdbapp.R

sealed class MovieError(
  open val messageResId: Int,
) {
  data class ApiError(
    val errorMessage: String,
    override val messageResId: Int = R.string.error_api,
  ) : MovieError(messageResId)

  object Network : MovieError(R.string.error_network)

  object Server : MovieError(R.string.error_server)

  object NoInternet : MovieError(R.string.error_no_internet)

  object ApiKeyMissing : MovieError(R.string.error_api_key_missing)

  object Unknown : MovieError(R.string.error_unknown)
}
