package com.example.tmdbapp.repository

import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.first

suspend fun Repository.askOpenAi(prompt: String): Resource<String> =
  safeApiCall {
    openAiApi.askOpenAi(apiKeyManager.openAiApiKeyFlow.first(), prompt)
  }
