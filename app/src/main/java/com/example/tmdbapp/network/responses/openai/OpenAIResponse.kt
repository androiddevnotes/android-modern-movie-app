package com.example.tmdbapp.network.responses.openai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
