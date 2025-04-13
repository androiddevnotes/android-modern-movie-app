package com.example.tmdbapp.repository

import android.content.Context
import com.example.tmdbapp.data.FavoritePreferencesDatastore
import com.example.tmdbapp.data.SessionManagerPreferencesDataStore
import com.example.tmdbapp.network.*
import com.example.tmdbapp.network.responses.tmdb.*
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber

class Repository(
  context: Context,
) {
  var tmdbApi: TmdbApiService = TmdbApiServiceImpl(KtorClient.httpClient)
  var openAiApi: OpenAiApiService = OpenAiApiServiceImpl(KtorClient.httpClient)
  var favoritePreferencesDatastore: FavoritePreferencesDatastore = FavoritePreferencesDatastore(context)
  var sessionManagerPreferencesDataStore: SessionManagerPreferencesDataStore = SessionManagerPreferencesDataStore(context)
  var apiKeyManager: ApiKeyManager = ApiKeyManager(context)

  suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> =
    try {
      val result = apiCall()
      Timber.d("Result: $result")
      Resource.Success(result)
    } catch (e: Exception) {
      Timber.e("Exception: ${e.message}")
      Resource.Error(e.message ?: "An unexpected error occurred")
    }
}

@Serializable
data class ErrorResponse(
  @SerialName("status_code") val statusCode: Int,
  @SerialName("status_message") val statusMessage: String,
  val success: Boolean,
)
