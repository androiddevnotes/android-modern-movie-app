package com.example.tmdbapp.repository

import android.content.Context
import com.example.tmdbapp.data.FavoritePreferences
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.models.MovieResponse
import com.example.tmdbapp.network.RetrofitInstance
import com.example.tmdbapp.utils.Resource

class MovieRepository(context: Context) {
    private val api = RetrofitInstance.api
    private val apiKey = "a3fa04090541f2bd7df49068a6259c18" // Replace with your TMDB API key
    private val favoritePreferences = FavoritePreferences(context)

    suspend fun getPopularMovies(page: Int): Resource<MovieResponse> {
        return try {
            val response = api.getPopularMovies(apiKey, page)
            val moviesWithFavoriteStatus = response.results.map { movie ->
                movie.copy(isFavorite = favoritePreferences.isFavorite(movie.id))
            }
            Resource.Success(response.copy(results = moviesWithFavoriteStatus))
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    suspend fun getFavoriteMovies(): List<Movie> {
        return try {
            val response = api.getPopularMovies(apiKey, 1) // Get first page to have some movie data
            response.results.filter { movie ->
                favoritePreferences.isFavorite(movie.id)
            }.map { it.copy(isFavorite = true) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun toggleFavorite(movie: Movie) {
        val newFavoriteStatus = !movie.isFavorite
        favoritePreferences.setFavorite(movie.id, newFavoriteStatus)
    }

    fun isFavorite(movieId: Int): Boolean {
        return favoritePreferences.isFavorite(movieId)
    }
}