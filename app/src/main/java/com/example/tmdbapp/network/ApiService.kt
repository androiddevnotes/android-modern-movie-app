package com.example.tmdbapp.network

import com.example.tmdbapp.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.*

class ApiService(
  private val client: HttpClient,
) {
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

  suspend fun discoverMovies(
    apiKey: String,
    page: Int,
    sortBy: String? = null,
    genres: String? = null,
    releaseYear: Int? = null,
    minRating: Float? = null,
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

  suspend fun searchMovies(
    apiKey: String,
    query: String,
    page: Int,
  ): MovieResponse = get("search/movie", apiKey, page, mapOf("query" to query))

  suspend fun getMovieDetails(
    movieId: Int,
    apiKey: String,
  ): Movie = get("movie/$movieId", apiKey, 1)

  suspend fun createRequestToken(apiKey: String): RequestTokenResponse = get("authentication/token/new", apiKey, 1)

  suspend fun createSession(
    apiKey: String,
    requestBody: CreateSessionRequest,
  ): CreateSessionResponse =
    client
      .post("authentication/session/new") {
        parameter("api_key", apiKey)
        setBody(requestBody)
      }.body()

  suspend fun createList(
    apiKey: String,
    sessionId: String,
    requestBody: CreateListRequest,
  ): CreateListResponse =
    client
      .post("list") {
        parameter("api_key", apiKey)
        parameter("session_id", sessionId)
        setBody(requestBody)
      }.body()
}

@Serializable
data class RequestTokenResponse(
  val success: Boolean,
  @SerialName("expires_at") val expiresAt: String,
  @SerialName("request_token") val requestToken: String,
)

@Serializable
data class CreateSessionRequest(
  @SerialName("request_token") val requestToken: String,
)

@Serializable
data class CreateSessionResponse(
  val success: Boolean,
  @SerialName("session_id") val sessionId: String,
)

@Serializable
data class CreateListRequest(
  val name: String,
  val description: String,
  val language: String = "en",
)

@Serializable
data class CreateListResponse(
  @SerialName("status_message") val statusMessage: String,
  val success: Boolean,
  @SerialName("status_code") val statusCode: Int,
  @SerialName("list_id") val listId: Int,
)
