package com.example.tmdbapp.network

interface OpenAiApiService {
  suspend fun askOpenAi(
    apiKey: String,
    prompt: String,
  ): String
}
