package com.example.tmdbapp.network.responses.openai

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIMessage(
  val role: String,
  val content: String,
)
