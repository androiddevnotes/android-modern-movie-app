package com.example.tmdbapp.network

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.responses.tmdb.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class TmdbApiService(
  private val client: HttpClient,
) : ApiService {
  private suspend inline fun <reified T> get(
    endpoint: String,
    apiKey: String,
    page: Int,
    additionalParams: Map<String, Any?> = emptyMap(),
  ): T =
    client
      .get(endpoint) {
        parameter("api_key", apiKey)
        parameter("page", page)
        additionalParams.forEach { (key, value) ->
          if (value != null) parameter(key, value)
        }
      }.body()

  override suspend fun discoverMovies(
    apiKey: String,
    page: Int,
    sortBy: String?,
    genres: String?,
    releaseYear: Int?,
    minRating: Float?,
  ): MovieResponse =
    get(
      "discover/movie",
      apiKey,
      page,
      mapOf(
        "sort_by" to sortBy,
        "with_genres" to genres,
        "primary_release_year" to releaseYear,
        "vote_average.gte" to minRating,
      ),
    )

  override suspend fun searchMovies(
    apiKey: String,
    query: String,
    page: Int,
  ): MovieResponse = get("search/movie", apiKey, page, mapOf("query" to query))

  override suspend fun getMovieDetails(
    movieId: Int,
    apiKey: String,
  ): Movie = get("movie/$movieId", apiKey, 1)

  override suspend fun createRequestToken(apiKey: String): RequestTokenResponse = get("authentication/token/new", apiKey, 1)

  override suspend fun createSession(
    apiKey: String,
    requestBody: CreateSessionRequest,
  ): CreateSessionResponse =
    client
      .post("authentication/session/new") {
        parameter("api_key", apiKey)
        contentType(ContentType.Application.Json)
        setBody(requestBody)
      }.body()

  override suspend fun createList(
    apiKey: String,
    sessionId: String,
    requestBody: CreateListRequest,
  ): CreateListResponse =
    client
      .post("list") {
        parameter("api_key", apiKey)
        parameter("session_id", sessionId)
        contentType(ContentType.Application.Json)
        setBody(requestBody)
      }.body()
}
