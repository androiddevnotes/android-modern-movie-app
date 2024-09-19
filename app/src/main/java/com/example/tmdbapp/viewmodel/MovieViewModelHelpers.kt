package com.example.tmdbapp.viewmodel

import com.example.tmdbapp.utils.MovieError
import retrofit2.HttpException
import java.io.IOException

fun MovieViewModel.handleError(errorMessage: String?): MovieError = MovieError.ApiError(errorMessage ?: "An unknown error occurred")

fun MovieViewModel.handleError(error: Throwable): MovieError =
  when (error) {
    is IOException -> MovieError.Network
    is HttpException -> {
      if (error.code() in 500..599) {
        MovieError.Server
      } else {
        MovieError.ApiError(error.message())
      }
    }
    else -> MovieError.Unknown
  }
