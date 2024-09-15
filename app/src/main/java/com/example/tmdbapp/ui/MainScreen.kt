package com.example.tmdbapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var currentThemeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

    NavGraph(
        navController = navController,
        movieViewModel = movieViewModel,
        currentThemeMode = currentThemeMode,
        onThemeChange = {
            currentThemeMode = when (currentThemeMode) {
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.SYSTEM
                ThemeMode.SYSTEM -> ThemeMode.LIGHT
            }
            onThemeChange(currentThemeMode)
        }
    )
}