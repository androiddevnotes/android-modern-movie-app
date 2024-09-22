package com.example.tmdbapp.network

interface OpenAiApiService {
  suspend fun askOpenAI(
    apiKey: String,
    prompt: String,
  ): String
}
