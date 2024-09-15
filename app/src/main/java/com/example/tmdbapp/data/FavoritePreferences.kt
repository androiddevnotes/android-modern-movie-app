package com.example.tmdbapp.data

import android.content.Context
import androidx.core.content.edit

class FavoritePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun setFavorite(movieId: Int, isFavorite: Boolean) {
        prefs.edit {
            putBoolean(movieId.toString(), isFavorite)
        }
    }

    fun isFavorite(movieId: Int): Boolean {
        return prefs.getBoolean(movieId.toString(), false)
    }
}