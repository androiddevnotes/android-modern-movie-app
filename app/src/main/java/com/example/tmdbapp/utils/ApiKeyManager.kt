package com.example.tmdbapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.tmdbapp.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ApiKeyManager(
  private val context: Context,
) {
  private val TMDB_API_KEY = stringPreferencesKey("TMDB_API_KEY")
  private val OPENAI_API_KEY = stringPreferencesKey("OPENAI_API_KEY")

  val tmdbApiKeyFlow: Flow<String> =
    context.apiKeyDataStore.data
      .map { preferences ->
        preferences[TMDB_API_KEY]
          ?.takeIf { it.isNotBlank() }
          ?: BuildConfig.TMDB_API_KEY.takeIf { it.isNotBlank() }
          ?: ""
      }

  val openAiApiKeyFlow: Flow<String> =
    context.apiKeyDataStore.data
      .map { preferences ->
        preferences[OPENAI_API_KEY]
          ?.takeIf { it.isNotBlank() }
          ?: BuildConfig.OPENAI_API_KEY.takeIf { it.isNotBlank() }
          ?: ""
      }

  suspend fun setTmdbApiKey(key: String) {
    context.apiKeyDataStore.edit { preferences ->
      preferences[TMDB_API_KEY] = key
    }
  }

  suspend fun setOpenAiApiKey(key: String) {
    context.apiKeyDataStore.edit { preferences ->
      preferences[OPENAI_API_KEY] = key
    }
  }
}

private val Context.apiKeyDataStore: DataStore<Preferences> by preferencesDataStore(name = "ApiKeys")
