package com.example.tmdbapp.network

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.responses.tmdb.*

interface TmdbApiService {
  suspend fun discoverMovies(
    apiKey: String,
    page: Int,
    sortBy: String? = null,
    genres: String? = null,
    releaseYear: Int? = null,
    minRating: Float? = null,
  ): MovieResponse

  suspend fun searchMovies(
    apiKey: String,
    query: String,
    page: Int,
  ): MovieResponse

  suspend fun getMovieDetails(
    movieId: Int,
    apiKey: String,
  ): Movie

  suspend fun createRequestToken(apiKey: String): RequestTokenResponse

  suspend fun createSession(
    apiKey: String,
    requestBody: CreateSessionRequest,
  ): CreateSessionResponse

  suspend fun createList(
    apiKey: String,
    sessionId: String,
    requestBody: CreateListRequest,
  ): CreateListResponse
}

interface OpenAiApiService {
  suspend fun askOpenAI(
    apiKey: String,
    prompt: String,
  ): String
}
