package com.example.tmdbapp.repository

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.responses.tmdb.*
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.*
import timber.log.Timber

suspend fun Repository.addFavoriteStatus(movies: List<Movie>): List<Movie> {
  val favorites = favoritePreferencesDatastore.getAllFavorites().first()
  return movies.map { movie ->
    movie.copy(isFavorite = favorites.contains(movie.id))
  }
}

fun Repository.getFavoriteMovies(): Flow<List<Movie>> =
  favoritePreferencesDatastore.getAllFavorites().map { favoriteIds ->
    favoriteIds.mapNotNull { movieId ->
      when (val result = getMovieDetails(movieId)) {
        is Resource.Success -> result.data
        is Resource.Error -> {
          Timber.e("Error fetching movie details for ID $movieId: ${result.message}")
          null
        }
      }
    }
  }

suspend fun Repository.toggleFavorite(movie: Movie) {
  val newFavoriteStatus = !movie.isFavorite
  favoritePreferencesDatastore.setFavorite(movie.id, newFavoriteStatus)
}

suspend fun Repository.searchMovies(
  query: String,
  page: Int,
): Resource<MovieResponse> =
  safeApiCall {
    val response = tmdbApi.searchMovies(apiKeyManager.tmdbApiKeyFlow.first(), query, page)
    response.copy(results = addFavoriteStatus(response.results))
  }

suspend fun Repository.getMovieDetails(movieId: Int): Resource<Movie> =
  safeApiCall {
    val response = tmdbApi.getMovieDetails(movieId, apiKeyManager.tmdbApiKeyFlow.first())
    val isFavorite = favoritePreferencesDatastore.isFavorite(response.id).first()
    response.copy(isFavorite = isFavorite)
  }

suspend fun Repository.createRequestToken(): Resource<String> =
  safeApiCall {
    val response = tmdbApi.createRequestToken(apiKeyManager.tmdbApiKeyFlow.first())
    if (response.success) {
      response.requestToken
    } else {
      throw Exception("Failed to create request token")
    }
  }

suspend fun Repository.createSession(approvedToken: String): Resource<String> =
  safeApiCall {
    val response = tmdbApi.createSession(apiKeyManager.tmdbApiKeyFlow.first(), CreateSessionRequest(approvedToken))
    if (response.success) {
      sessionManagerPreferencesDataStore.saveSessionId(response.sessionId)
      response.sessionId
    } else {
      throw Exception("Failed to create session")
    }
  }

suspend fun Repository.createList(
  name: String,
  description: String,
): Resource<Int> =
  safeApiCall {
    val sessionId = sessionManagerPreferencesDataStore.sessionIdFlow.first() ?: throw Exception("No active session")
    val response =
      tmdbApi.createList(
        apiKeyManager.tmdbApiKeyFlow.first(),
        sessionId,
        CreateListRequest(name = name, description = description),
      )
    if (response.success && response.listId != null) {
      response.listId
    } else {
      throw Exception(response.statusMessage ?: "Failed to create list")
    }
  }

suspend fun Repository.discoverMovies(
  page: Int,
  sortBy: String? = null,
  genres: List<Int>? = null,
  releaseYear: Int? = null,
  minRating: Float? = null,
): Resource<MovieResponse> =
  safeApiCall {
    val response =
      tmdbApi.discoverMovies(
        apiKeyManager.tmdbApiKeyFlow.first(),
        page,
        sortBy,
        genres?.joinToString(","),
        releaseYear,
        minRating,
      )
    response.copy(results = addFavoriteStatus(response.results))
  }
