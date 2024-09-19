package com.example.tmdbapp.data

import android.content.*
import androidx.core.content.*
import com.example.tmdbapp.utils.*

class FavoritePreferences(
  context: Context,
) {
  private val prefs =
    context.getSharedPreferences(Constants.PREFS_NAME_FAVORITES, Context.MODE_PRIVATE)

  fun setFavorite(
    movieId: Int,
    isFavorite: Boolean,
  ) {
    prefs.edit {
      putBoolean(movieId.toString(), isFavorite)
    }
  }

  fun isFavorite(movieId: Int): Boolean = prefs.getBoolean(movieId.toString(), false)
}
