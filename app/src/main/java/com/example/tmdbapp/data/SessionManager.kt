package com.example.tmdbapp.data

import android.content.*
import kotlinx.coroutines.flow.*

class SessionManager(
  context: Context,
) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
  private val editor: SharedPreferences.Editor = prefs.edit()

  private val _sessionIdFlow = MutableStateFlow<String?>(null)
  val sessionIdFlow: Flow<String?> = _sessionIdFlow.asStateFlow()

  init {
    _sessionIdFlow.value = prefs.getString(SESSION_ID_KEY, null)
  }

  fun saveSessionId(sessionId: String) {
    editor.putString(SESSION_ID_KEY, sessionId).apply()
    _sessionIdFlow.value = sessionId
  }

  fun getSessionId(): String? = prefs.getString(SESSION_ID_KEY, null)

  companion object {
    private const val PREF_NAME = "TMDBSessionPrefs"
    private const val SESSION_ID_KEY = "session_id"
  }
}
