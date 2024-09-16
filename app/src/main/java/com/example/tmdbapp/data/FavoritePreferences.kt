package com.example.tmdbapp.data

import android.content.Context
import androidx.core.content.edit
import com.example.tmdbapp.utils.Constants

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
