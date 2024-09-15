package com.example.tmdbapp.utils

import com.example.tmdbapp.utils.Constants

sealed class MovieError(val message: String) {
    object Network : MovieError(Constants.ERROR_NETWORK)
    object Server : MovieError(Constants.ERROR_SERVER)
    data class ApiError(val errorMessage: String) : MovieError(errorMessage)
    object Unknown : MovieError(Constants.ERROR_UNKNOWN)
}