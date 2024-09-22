package com.example.tmdbapp.repository

import android.content.Context
import com.example.tmdbapp.data.FavoritePreferencesDatastore
import com.example.tmdbapp.data.SessionManagerPreferencesDataStore
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.*
import com.example.tmdbapp.network.responses.tmdb.*
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class Repository(
  context: Context,
) {
  private val tmdbApi = TmdbApiServiceImpl(KtorClient.httpClient)
  private val openAIApi = OpenAiApiServiceImpl(KtorClient.httpClient)
  private val favoritePreferencesDatastore = FavoritePreferencesDatastore(context)
  private val sessionManagerPreferencesDataStore = SessionManagerPreferencesDataStore(context)
  private val apiKeyManager = ApiKeyManager(context)

  private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> =
    try {
      Resource.Success(apiCall())
    } catch (e: Exception) {
      Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
    }

  private suspend fun addFavoriteStatus(movies: List<Movie>): List<Movie> {
    val favorites = favoritePreferencesDatastore.getAllFavorites().first()
    return movies.map { movie ->
      movie.copy(isFavorite = favorites.contains(movie.id))
    }
  }

  suspend fun getPopularMovies(page: Int): Resource<MovieResponse> = discoverMovies(page, sortBy = "popularity.desc")

  fun getFavoriteMovies(): Flow<List<Movie>> =
    favoritePreferencesDatastore.getAllFavorites().map { favoriteIds ->
      safeApiCall {
        tmdbApi.discoverMovies(apiKeyManager.getTmdbApiKey(), 1, sortBy = "popularity.desc")
      }.let { result ->
        when (result) {
          is Resource.Success -> {
            result.data
              ?.results
              ?.filter { movie -> favoriteIds.contains(movie.id) }
              ?.map { it.copy(isFavorite = true) }
              ?: emptyList()
          }
          is Resource.Error -> emptyList()
        }
      }
    }

  suspend fun toggleFavorite(movie: Movie) {
    val newFavoriteStatus = !movie.isFavorite
    favoritePreferencesDatastore.setFavorite(movie.id, newFavoriteStatus)
  }

  suspend fun discoverMovies(
    page: Int,
    sortBy: String? = null,
    genres: List<Int>? = null,
    releaseYear: Int? = null,
    minRating: Float? = null,
  ): Resource<MovieResponse> =
    safeApiCall {
      val response =
        tmdbApi.discoverMovies(
          apiKeyManager.getTmdbApiKey(),
          page,
          sortBy,
          genres?.joinToString(","),
          releaseYear,
          minRating,
        )
      response.copy(results = addFavoriteStatus(response.results))
    }

  suspend fun searchMovies(
    query: String,
    page: Int,
  ): Resource<MovieResponse> =
    safeApiCall {
      val response = tmdbApi.searchMovies(apiKeyManager.getTmdbApiKey(), query, page)
      response.copy(results = addFavoriteStatus(response.results))
    }

  suspend fun getMovieDetails(movieId: Int): Movie? =
    try {
      val response = tmdbApi.getMovieDetails(movieId, apiKeyManager.getTmdbApiKey())
      val isFavorite = favoritePreferencesDatastore.isFavorite(response.id).first()
      response.copy(isFavorite = isFavorite)
    } catch (e: Exception) {
      null
    }

  suspend fun createRequestToken(): Resource<String> =
    try {
      val response = tmdbApi.createRequestToken(apiKeyManager.getTmdbApiKey())
      if (response.success) {
        Resource.Success(response.requestToken)
      } else {
        Resource.Error("Failed to create request token")
      }
    } catch (e: Exception) {
      Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
    }

  suspend fun createSession(approvedToken: String): Resource<String> =
    try {
      val response =
        tmdbApi.createSession(apiKeyManager.getTmdbApiKey(), CreateSessionRequest(approvedToken))
      if (response.success) {
        sessionManagerPreferencesDataStore.saveSessionId(response.sessionId)
        Resource.Success(response.sessionId)
      } else {
        Resource.Error("Failed to create session")
      }
    } catch (e: Exception) {
      Resource.Error(e.localizedMessage ?: "An unknown error occurred")
    }

  suspend fun createList(
    name: String,
    description: String,
  ): Resource<Int> {
    return try {
      val sessionId = sessionManagerPreferencesDataStore.sessionIdFlow.first() ?: return Resource.Error("No active session")
      val response =
        tmdbApi.createList(
          apiKeyManager.getTmdbApiKey(),
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

  suspend fun askOpenAI(prompt: String): String = openAIApi.askOpenAI(apiKeyManager.getOpenAiApiKey(), prompt)
}