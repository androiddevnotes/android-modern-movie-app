package com.example.tmdbapp.network

import com.example.tmdbapp.network.responses.openai.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class OpenAiApiApiServiceImpl(
  private val client: HttpClient,
) : OpenAiApiService {
  override suspend fun askOpenAI(
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
