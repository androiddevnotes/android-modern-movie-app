package com.example.tmdbapp.network

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.models.MovieResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

class ApiService(
  private val client: HttpClient,
) {
  suspend fun getPopularMovies(
    apiKey: String,
    page: Int,
  ): MovieResponse =
    client
      .get("movie/popular") {
        parameter("api_key", apiKey)
        parameter("page", page)
      }.body()

  suspend fun getNowPlayingMovies(
    apiKey: String,
    page: Int,
  ): MovieResponse =
    client
      .get("movie/now_playing") {
        parameter("api_key", apiKey)
        parameter("page", page)
      }.body()

  suspend fun getTopRatedMovies(
    apiKey: String,
    page: Int,
  ): MovieResponse =
    client
      .get("movie/top_rated") {
        parameter("api_key", apiKey)
        parameter("page", page)
      }.body()

  suspend fun getUpcomingMovies(
    apiKey: String,
    page: Int,
  ): MovieResponse =
    client
      .get("movie/upcoming") {
        parameter("api_key", apiKey)
        parameter("page", page)
      }.body()

  suspend fun discoverMovies(
    apiKey: String,
    page: Int,
    sortBy: String? = null,
    genres: String? = null,
    releaseYear: Int? = null,
    minRating: Float? = null,
  ): MovieResponse =
    client
      .get("discover/movie") {
        parameter("api_key", apiKey)
        parameter("page", page)
        sortBy?.let { parameter("sort_by", it) }
        genres?.let { parameter("with_genres", it) }
        releaseYear?.let { parameter("primary_release_year", it) }
        minRating?.let { parameter("vote_average.gte", it) }
      }.body()

  suspend fun searchMovies(
    apiKey: String,
    query: String,
    page: Int,
  ): MovieResponse =
    client
      .get("search/movie") {
        parameter("api_key", apiKey)
        parameter("query", query)
        parameter("page", page)
      }.body()

  suspend fun getMovieDetails(
    movieId: Int,
    apiKey: String,
  ): Movie =
    client
      .get("movie/$movieId") {
        parameter("api_key", apiKey)
      }.body()

  suspend fun createRequestToken(apiKey: String): RequestTokenResponse =
    client
      .get("authentication/token/new") {
        parameter("api_key", apiKey)
      }.body()

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
  val expires_at: String,
  val request_token: String,
)

@Serializable
data class CreateSessionRequest(
  val requestToken: String,
)

@Serializable
data class CreateSessionResponse(
  val success: Boolean,
  val session_id: String,
)

@Serializable
data class CreateListRequest(
  val name: String,
  val description: String,
  val language: String = "en",
)

@Serializable
data class CreateListResponse(
  val status_message: String,
  val success: Boolean,
  val status_code: Int,
  val list_id: Int,
)
