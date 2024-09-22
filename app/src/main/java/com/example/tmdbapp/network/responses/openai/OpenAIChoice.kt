package com.example.tmdbapp.network.responses.openai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChoice(
  val index: Int,
  val message: OpenAIMessage,
  @SerialName("finish_reason") val finishReason: String,
)
