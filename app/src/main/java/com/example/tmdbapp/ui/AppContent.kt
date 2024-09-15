package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.tmdbapp.ui.theme.TMDBAppTheme
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun AppContent(
    movieViewModel: MovieViewModel,
    themeMode: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit
) {
    TMDBAppTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainScreen(
                movieViewModel = movieViewModel,
                onThemeChange = onThemeChange
            )
        }
    }
}