package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.MovieError
import io.ktor.client.plugins.*
import java.io.IOException

fun handleError(errorMessage: String?): MovieError = MovieError.ApiError(errorMessage ?: "An unknown error occurred")

fun handleError(error: Throwable): MovieError? =
  when (error) {
    is IOException -> MovieError.Network
    is ResponseException -> {
      when (error.response.status.value) {
        in 500..599 -> MovieError.Server
        else -> error.message?.let { MovieError.ApiError(it) }
      }
    }
    else -> MovieError.Unknown
  }
