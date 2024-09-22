package com.example.tmdbapp.network

import com.example.tmdbapp.network.responses.openai.*
import com.example.tmdbapp.repository.ErrorResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

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

    val response =
      client
        .post("https://resident.ngrok.app/chat/completions") {
          header("Authorization", "Bearer $apiKey")
          contentType(ContentType.Application.Json)
          setBody(openAIRequest)
        }.body<String>()

    val json = Json { ignoreUnknownKeys = true }
    return try {
      val openAiResponse = json.decodeFromString<OpenAiResponse>(response)
      openAiResponse.choices
        .firstOrNull()
        ?.message
        ?.content ?: throw Exception("No response generated.")
    } catch (e: SerializationException) {
      val errorResponse = json.decodeFromString<ErrorResponse>(response)
      throw Exception(errorResponse.statusMessage)
    }
  }
}
