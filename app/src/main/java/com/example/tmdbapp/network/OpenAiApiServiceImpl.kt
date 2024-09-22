package com.example.tmdbapp.network

import com.example.tmdbapp.network.responses.openai.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class OpenAiApiServiceImpl(
  private val client: HttpClient,
) : OpenAiApiService {
  override suspend fun askOpenAi(
    apiKey: String,
    prompt: String,
  ): String {
    val openAIRequest =
      OpenAiRequest(
        model = "gpt-3.5-turbo",
        messages = listOf(OpenAiMessage(role = "user", content = prompt)),
      )

    val response: OpenAiResponse =
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
