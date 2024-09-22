package com.example.tmdbapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "TMDBSessionPrefs")

class SessionManager(
  private val context: Context,
) {
  private val SESSION_ID_KEY = stringPreferencesKey("session_id")

  val sessionIdFlow: Flow<String?> =
    context.sessionDataStore.data
      .map { preferences ->
        preferences[SESSION_ID_KEY]
      }

  suspend fun saveSessionId(sessionId: String) {
    context.sessionDataStore.edit { preferences ->
      preferences[SESSION_ID_KEY] = sessionId
    }
  }

  suspend fun clearSessionId() {
    context.sessionDataStore.edit { preferences ->
      preferences.remove(SESSION_ID_KEY)
    }
  }
}
