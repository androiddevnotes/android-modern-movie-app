package com.example.tmdbapp.repository

import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.first

suspend fun Repository.askOpenAi(prompt: String): Resource<String> =
  safeApiCall {
    val openAiApiKey = apiKeyManager.openAiApiKeyFlow.first()
    openAiApi.askOpenAi(openAiApiKey, prompt)
  }
