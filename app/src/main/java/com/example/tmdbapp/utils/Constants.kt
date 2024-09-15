package com.example.tmdbapp.utils

import androidx.compose.ui.unit.dp

object Constants {
    // Dimensions
    val PADDING_SMALL = 8.dp
    val PADDING_MEDIUM = 16.dp
    val CARD_ELEVATION = 8.dp
    val CARD_CORNER_RADIUS = 16.dp
    val ICON_SIZE_SMALL = 28.dp
    val ICON_SIZE_MEDIUM = 48.dp
    val MOVIE_ITEM_HEIGHT = 240.dp

    // API related
    const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"

    // Screen titles
    const val SCREEN_TITLE_FAVORITES = "Favorites"
    const val SCREEN_TITLE_DISCOVER = "Discover Movies"

    // Content descriptions
    const val CONTENT_DESC_BACK = "Back"
    const val CONTENT_DESC_FAVORITES = "Favorites"

    // Messages
    const val MESSAGE_NO_FAVORITES = "No favorites yet"
}