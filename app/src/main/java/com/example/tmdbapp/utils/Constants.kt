package com.example.tmdbapp.utils

import androidx.compose.ui.unit.*

object Constants {
  val PADDING_SMALL = 8.dp
  val PADDING_MEDIUM = 16.dp
  val ICON_SIZE_SMALL = 28.dp
  val ICON_SIZE_MEDIUM = 48.dp

  const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"
  const val BASE_API_URL = "https://api.themoviedb.org/3/"

  const val SCREEN_TITLE_FAVORITES = "Favorites"

  const val MESSAGE_NO_FAVORITES = "No favorites yet"

  const val VIEW_TYPE_GRID = "grid"
  const val VIEW_TYPE_LIST = "list"

  const val PREFS_NAME_FAVORITES = "favorites"

  const val DELAY_SEARCH = 300L
}
