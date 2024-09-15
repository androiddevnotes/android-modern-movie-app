package com.example.tmdbapp.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
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

    fun getSessionId(): String? {
        return prefs.getString(SESSION_ID_KEY, null)
    }

    fun clearSessionId() {
        editor.remove(SESSION_ID_KEY).apply()
        _sessionIdFlow.value = null
    }

    companion object {
        private const val PREF_NAME = "TMDBSessionPrefs"
        private const val SESSION_ID_KEY = "session_id"
    }
}