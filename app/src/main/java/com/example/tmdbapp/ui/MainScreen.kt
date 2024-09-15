package com.example.tmdbapp.ui

import androidx.compose.runtime.*
import androidx.activity.compose.BackHandler
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.viewmodel.MovieViewModel
import com.example.tmdbapp.utils.Constants

sealed class Screen {
    object List : Screen()
    object Favorites : Screen()
    data class Detail(val movie: Movie) : Screen()
}

@Composable
fun MainScreen(viewModel: MovieViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
    var previousScreen by remember { mutableStateOf<Screen>(Screen.List) }
    val selectedMovie by viewModel.selectedMovie.collectAsState()

    BackHandler(enabled = currentScreen != Screen.List) {
        when (currentScreen) {
            is Screen.Detail -> {
                viewModel.clearSelectedMovie()
                currentScreen = previousScreen
            }
            is Screen.Favorites -> {
                currentScreen = Screen.List
                previousScreen = Screen.List
            }
            is Screen.List -> {}
        }
    }

    when (currentScreen) {
        is Screen.List -> MovieListScreen(
            viewModel = viewModel,
            onMovieClick = { 
                viewModel.selectMovie(it) 
                previousScreen = Screen.List
                currentScreen = Screen.Detail(it)
            },
            onFavoritesClick = { 
                currentScreen = Screen.Favorites
                previousScreen = Screen.List
            },
            screenTitle = Constants.SCREEN_TITLE_DISCOVER
        )
        is Screen.Favorites -> FavoritesScreen(
            viewModel = viewModel,
            onMovieClick = { movieId ->
                val movie = viewModel.getMovieById(movieId)
                if (movie != null) {
                    viewModel.selectMovie(movie)
                    previousScreen = Screen.Favorites
                    currentScreen = Screen.Detail(movie)
                }
            },
            onBackPress = {
                currentScreen = Screen.List
                previousScreen = Screen.List
            }
        )
        is Screen.Detail -> MovieDetailScreen(
            viewModel = viewModel,
            onBackPress = {
                viewModel.clearSelectedMovie()
                currentScreen = previousScreen
            }
        )
    }
}