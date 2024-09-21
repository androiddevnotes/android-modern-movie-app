package com.example.tmdbapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.tmdbapp.BuildConfig

class ApiKeyManager(
  context: Context,
) {
  private val sharedPreferences: SharedPreferences = context.getSharedPreferences("ApiKeys", Context.MODE_PRIVATE)

  fun getTmdbApiKey(): String {
    val savedKey = sharedPreferences.getString("TMDB_API_KEY", null)
    return savedKey?.takeIf { it.isNotBlank() }
      ?: BuildConfig.TMDB_API_KEY.takeIf { it.isNotBlank() }
      ?: ""
  }

  fun setTmdbApiKey(key: String) {
    sharedPreferences.edit {
      putString("TMDB_API_KEY", key)
    }
  }

  fun getOpenAiApiKey(): String {
    val savedKey = sharedPreferences.getString("OPENAI_API_KEY", null)
    return savedKey?.takeIf { it.isNotBlank() }
      ?: BuildConfig.OPENAI_API_KEY.takeIf { it.isNotBlank() }
      ?: ""
  }

  fun setOpenAiApiKey(key: String) {
    sharedPreferences.edit {
      putString("OPENAI_API_KEY", key)
    }
  }
}
