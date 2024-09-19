package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.*
import io.ktor.client.plugins.*
import java.io.*
import java.net.UnknownHostException

fun handleError(errorMessage: String?): MovieError = MovieError.ApiError(errorMessage ?: "An unknown error occurred")

fun handleError(error: Throwable): MovieError =
  when (error) {
    is UnknownHostException -> MovieError.NoInternet
    is IOException -> MovieError.Network
    is ResponseException -> {
      when (error.response.status.value) {
        in 500..599 -> MovieError.Server
        else -> MovieError.ApiError(error.message ?: "Unknown API error")
      }
    }
    else -> MovieError.Unknown
  }
