package com.example.tmdbapp.repository

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.responses.tmdb.*
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.*

suspend fun Repository.addFavoriteStatus(movies: List<Movie>): List<Movie> {
  val favorites = favoritePreferencesDatastore.getAllFavorites().first()
  return movies.map { movie ->
    movie.copy(isFavorite = favorites.contains(movie.id))
  }
}

fun Repository.getFavoriteMovies(): Flow<List<Movie>> =
  favoritePreferencesDatastore.getAllFavorites().map { favoriteIds ->
    favoriteIds.mapNotNull { movieId ->
      getMovieDetails(movieId)
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

suspend fun Repository.getMovieDetails(movieId: Int): Movie? =
  try {
    val response = tmdbApi.getMovieDetails(movieId, apiKeyManager.tmdbApiKeyFlow.first())
    val isFavorite = favoritePreferencesDatastore.isFavorite(response.id).first()
    response.copy(isFavorite = isFavorite)
  } catch (e: Exception) {
    null
  }

suspend fun Repository.createRequestToken(): Resource<String> =
  try {
    val response = tmdbApi.createRequestToken(apiKeyManager.tmdbApiKeyFlow.first())
    if (response.success) {
      Resource.Success(response.requestToken)
    } else {
      Resource.Error("Failed to create request token")
    }
  } catch (e: Exception) {
    Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
  }

suspend fun Repository.createSession(approvedToken: String): Resource<String> =
  try {
    val response =
      tmdbApi.createSession(apiKeyManager.tmdbApiKeyFlow.first(), CreateSessionRequest(approvedToken))
    if (response.success) {
      sessionManagerPreferencesDataStore.saveSessionId(response.sessionId)
      Resource.Success(response.sessionId)
    } else {
      Resource.Error("Failed to create session")
    }
  } catch (e: Exception) {
    Resource.Error(e.localizedMessage ?: "An unknown error occurred")
  }

suspend fun Repository.createList(
  name: String,
  description: String,
): Resource<Int> {
  return try {
    val sessionId = sessionManagerPreferencesDataStore.sessionIdFlow.first() ?: return Resource.Error("No active session")
    val response =
      tmdbApi.createList(
        apiKeyManager.tmdbApiKeyFlow.first(),
        sessionId,
        CreateListRequest(name = name, description = description),
      )
    if (response.success) {
      Resource.Success(response.listId)
    } else {
      Resource.Error(response.statusMessage)
    }
  } catch (e: Exception) {
    Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
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
