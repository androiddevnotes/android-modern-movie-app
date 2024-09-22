package com.example.tmdbapp.network.responses.openai

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiErrorResponse(
  val error: OpenAiError,
)

@Serializable
data class OpenAiError(
  val message: String,
  val type: String,
  val param: String?,
  val code: String,
)
