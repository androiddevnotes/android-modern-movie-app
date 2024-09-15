package com.example.tmdbapp.repository

import com.example.tmdbapp.models.MovieResponse
import com.example.tmdbapp.network.RetrofitInstance
import com.example.tmdbapp.utils.Resource

class MovieRepository {
    private val api = RetrofitInstance.api
    private val apiKey = "a3fa04090541f2bd7df49068a6259c18" // Replace with your TMDB API key

    suspend fun getPopularMovies(page: Int): Resource<MovieResponse> {
        return try {
            val response = api.getPopularMovies(apiKey, page)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}