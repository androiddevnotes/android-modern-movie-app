package com.example.tmdbapp.network.responses.openai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAiMessage(
  val content: String,
  val role: String,
)

@Serializable
data class OpenAiChoice(
  @SerialName("finish_reason") val finishReason: String,
  val index: Int,
  val message: OpenAiMessage,
)

@Serializable
data class OpenAiUsage(
  @SerialName("completion_tokens") val completionTokens: Int,
  @SerialName("prompt_tokens") val promptTokens: Int,
  @SerialName("total_tokens") val totalTokens: Int,
)

@Serializable
data class OpenAiResponse(
  val choices: List<OpenAiChoice>,
  val created: Long,
  val id: String,
  val model: String,
  val `object`: String,
  @SerialName("system_fingerprint") val systemFingerprint: String?,
  val usage: OpenAiUsage,
)
