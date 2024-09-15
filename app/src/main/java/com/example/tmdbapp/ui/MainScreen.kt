package com.example.tmdbapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.tmdbapp.ui.theme.LocalThemeMode
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun MainScreen(
    movieViewModel: MovieViewModel,
    onThemeChange: (ThemeMode) -> Unit
) {
    val navController = rememberNavController()
    val currentThemeMode = LocalThemeMode.current

    NavGraph(
        navController = navController,
        movieViewModel = movieViewModel,
        currentThemeMode = currentThemeMode,
        onThemeChange = { onThemeChange(currentThemeMode) }
    )
}