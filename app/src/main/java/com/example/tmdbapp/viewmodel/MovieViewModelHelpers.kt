package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.*
import io.ktor.client.plugins.*
import java.io.*

fun handleError(errorMessage: String?): MovieError =
  when {
    errorMessage?.contains("UnknownHostException") == true -> MovieError.NoInternet
    errorMessage?.contains("IOException") == true -> MovieError.Network
    errorMessage?.contains("ResponseException") == true -> {
      if (errorMessage.contains("5")) {
        MovieError.Server
      } else {
        MovieError.ApiError(errorMessage)
      }
    }
    else -> MovieError.Unknown
  }

// ... rest of the file ...
