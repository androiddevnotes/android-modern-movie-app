package com.example.tmdbapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.tmdbapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.PREFS_NAME_FAVORITES)

class FavoritePreferences(
  private val context: Context,
) {
  suspend fun setFavorite(
    movieId: Int,
    isFavorite: Boolean,
  ) {
    context.dataStore.edit { preferences ->
      preferences[booleanPreferencesKey(movieId.toString())] = isFavorite
    }
  }

  fun isFavorite(movieId: Int): Flow<Boolean> =
    context.dataStore.data
      .map { preferences ->
        preferences[booleanPreferencesKey(movieId.toString())] ?: false
      }

  fun getAllFavorites(): Flow<Set<Int>> =
    context.dataStore.data
      .map { preferences ->
        preferences
          .asMap()
          .filterValues { it == true }
          .keys
          .mapNotNull { it.name.toIntOrNull() }
          .toSet()
      }
}
