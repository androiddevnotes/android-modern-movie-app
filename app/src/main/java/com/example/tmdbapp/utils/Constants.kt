package com.example.tmdbapp.utils

import androidx.compose.ui.unit.dp

object Constants {
    
    val PADDING_SMALL = 8.dp
    val PADDING_MEDIUM = 16.dp
    val CARD_ELEVATION = 8.dp
    val CARD_CORNER_RADIUS = 16.dp
    val ICON_SIZE_SMALL = 28.dp
    val ICON_SIZE_MEDIUM = 48.dp
    val MOVIE_ITEM_HEIGHT = 240.dp

    
    const val BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w500"
    const val BASE_API_URL = "https://api.themoviedb.org/3/"

    
    const val SCREEN_TITLE_FAVORITES = "Favorites"
    const val SCREEN_TITLE_DISCOVER = ""

    
    const val CONTENT_DESC_BACK = "Back"
    const val CONTENT_DESC_FAVORITES = "Favorites"
    const val CONTENT_DESC_SWITCH_VIEW = "Switch view"
    const val CONTENT_DESC_TOGGLE_THEME = "Toggle theme"

    
    const val MESSAGE_NO_FAVORITES = "No favorites yet"
    const val MESSAGE_UNKNOWN_ERROR = "Unknown error occurred."

    
    const val VIEW_TYPE_GRID = "grid"
    const val VIEW_TYPE_LIST = "list"

    
    const val PREFS_NAME_FAVORITES = "favorites"

    
    const val ERROR_NETWORK = "Network error. Please check your internet connection."
    const val ERROR_SERVER = "Server error. Please try again later."
    const val ERROR_UNKNOWN = "An unknown error occurred. Please try again."

    
    const val GRID_COLUMNS_ADAPTIVE_WIDTH = 150
    const val ALPHA_MEDIUM = 0.8f
    const val MAX_LINES_TITLE = 2
    const val MAX_LINES_OVERVIEW = 3
    const val DELAY_SEARCH = 300L
}