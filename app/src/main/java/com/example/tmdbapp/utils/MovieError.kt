package com.example.tmdbapp.utils

sealed class MovieError(val message: String) {
    object Network : MovieError("Network error. Please check your internet connection.")
    object Server : MovieError("Server error. Please try again later.")
    data class ApiError(val errorMessage: String) : MovieError(errorMessage)
    object Unknown : MovieError("An unknown error occurred. Please try again.")
}