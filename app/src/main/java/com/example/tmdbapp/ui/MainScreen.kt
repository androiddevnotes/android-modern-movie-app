package com.example.tmdbapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun MainScreen(
    movieViewModel: MovieViewModel,
    onThemeChange: (ThemeMode) -> Unit
) {
    val navController = rememberNavController()
    var currentThemeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
    var viewType by rememberSaveable { mutableStateOf(Constants.VIEW_TYPE_GRID) }

    NavGraph(
        navController = navController,
        movieViewModel = movieViewModel,
        currentThemeMode = currentThemeMode,
        onThemeChange = {
            // ... (theme change logic)
        },
        viewType = viewType,
        onViewTypeChange = { newViewType ->
            viewType = newViewType
        }
    )
}