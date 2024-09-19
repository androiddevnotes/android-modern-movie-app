package com.example.tmdbapp.utils

import androidx.annotation.*
import com.example.tmdbapp.*

sealed class MovieError(
  @StringRes val messageResId: Int,
) {
  object Network : MovieError(R.string.error_network)

  object Server : MovieError(R.string.error_server)

  object NoInternet : MovieError(R.string.error_no_internet)

  data class ApiError(
    val errorMessage: String,
  ) : MovieError(R.string.error_unknown)

  object Unknown : MovieError(R.string.error_unknown)
}
