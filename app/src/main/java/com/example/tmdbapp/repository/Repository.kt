package com.example.tmdbapp.repository

import android.content.Context
import com.example.tmdbapp.data.FavoritePreferencesDatastore
import com.example.tmdbapp.data.SessionManagerPreferencesDataStore
import com.example.tmdbapp.network.*
import com.example.tmdbapp.network.responses.tmdb.*
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource

class Repository(
  context: Context,
) {
  val tmdbApi = TmdbApiServiceImpl(KtorClient.httpClient)
  val openAiApi = OpenAiApiServiceImpl(KtorClient.httpClient)
  val favoritePreferencesDatastore = FavoritePreferencesDatastore(context)
  val sessionManagerPreferencesDataStore = SessionManagerPreferencesDataStore(context)
  val apiKeyManager = ApiKeyManager(context)

  suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> =
    try {
      Resource.Success(apiCall())
    } catch (e: Exception) {
      Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
    }
}
