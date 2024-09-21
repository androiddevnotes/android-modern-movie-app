package com.example.tmdbapp.network

import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.models.MovieResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
  ): MovieResponse {
    kotlinx.coroutines.delay(5000) // Add a 5-second delay
    return get(
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
  }

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

  suspend fun askOpenAI(
    apiKey: String,
    prompt: String,
  ): String {
    val openAIRequest =
      OpenAIRequest(
        model = "gpt-3.5-turbo",
        messages = listOf(OpenAIMessage(role = "user", content = prompt)),
      )

    val response: OpenAIResponse =
      client
        .post("https://api.openai.com/v1/chat/completions") {
          header("Authorization", "Bearer $apiKey")
          contentType(ContentType.Application.Json)
          setBody(openAIRequest)
        }.body()

    return response.choices
      .firstOrNull()
      ?.message
      ?.content ?: "No response generated."
  }
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

@Serializable
data class OpenAIRequest(
  val model: String,
  val messages: List<OpenAIMessage>,
)

@Serializable
data class OpenAIResponse(
  val id: String,
  val `object`: String,
  val created: Long,
  val model: String,
  @SerialName("system_fingerprint") val systemFingerprint: String?,
  val choices: List<OpenAIChoice>,
  val usage: OpenAIUsage,
)

@Serializable
data class OpenAIChoice(
  val index: Int,
  val message: OpenAIMessage,
  @SerialName("finish_reason") val finishReason: String,
)

@Serializable
data class OpenAIMessage(
  val role: String,
  val content: String,
)

@Serializable
data class OpenAIUsage(
  @SerialName("prompt_tokens") val promptTokens: Int,
  @SerialName("completion_tokens") val completionTokens: Int,
  @SerialName("total_tokens") val totalTokens: Int,
)
