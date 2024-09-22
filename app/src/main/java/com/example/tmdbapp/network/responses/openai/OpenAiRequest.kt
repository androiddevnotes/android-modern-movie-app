package com.example.tmdbapp.network.responses.openai

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiRequest(
  val messages: List<OpenAiMessage>,
  val model: String,
)
