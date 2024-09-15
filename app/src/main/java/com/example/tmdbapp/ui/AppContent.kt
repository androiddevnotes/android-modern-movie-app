package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tmdbapp.ui.theme.TMDBAppTheme
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun AppContent(
    movieViewModel: MovieViewModel,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    var viewType by rememberSaveable { mutableStateOf(Constants.VIEW_TYPE_GRID) }

    TMDBAppTheme(themeMode = themeMode) {
        NavGraph(
            movieViewModel = movieViewModel,
            viewType = viewType,
            onViewTypeChange = { viewType = it },
            onThemeChange = onThemeChange,
            currentThemeMode = themeMode
        )
    }
}