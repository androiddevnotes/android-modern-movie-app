package com.example.tmdbapp.utils

import androidx.annotation.StringRes
import com.example.tmdbapp.R

sealed class MovieError(
  @StringRes val messageResId: Int,
) {
  object Network : MovieError(R.string.error_network)

  object Server : MovieError(R.string.error_server)

  object NoInternet : MovieError(R.string.error_no_internet)

  object ApiKeyMissing : MovieError(R.string.error_api_key_missing)

  data class ApiError(
    val errorCode: Int,
    val errorMessage: String,
  ) : MovieError(R.string.error_unknown)

  object Unknown : MovieError(R.string.error_unknown)
}
