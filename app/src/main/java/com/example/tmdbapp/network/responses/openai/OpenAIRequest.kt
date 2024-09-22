package com.example.tmdbapp.network.responses.openai

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIRequest(
  val model: String,
  val messages: List<OpenAIMessage>,
)
