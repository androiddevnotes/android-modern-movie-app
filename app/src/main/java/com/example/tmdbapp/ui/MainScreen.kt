package com.example.tmdbapp.ui

import androidx.compose.runtime.*
import androidx.activity.compose.BackHandler
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.viewmodel.MovieViewModel

sealed class Screen {
    object List : Screen()
    object Favorites : Screen()
    data class Detail(val movie: Movie) : Screen()
}

@Composable
fun MainScreen(viewModel: MovieViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
    val selectedMovie by viewModel.selectedMovie.collectAsState()

    BackHandler(enabled = currentScreen != Screen.List) {
        when (currentScreen) {
            is Screen.Detail -> {
                viewModel.clearSelectedMovie()
                currentScreen = Screen.List
            }
            is Screen.Favorites -> {
                currentScreen = Screen.List
            }
            is Screen.List -> {}
        }
    }

    when (currentScreen) {
        is Screen.List -> MovieListScreen(
            viewModel = viewModel,
            onMovieClick = { 
                viewModel.selectMovie(it) 
                currentScreen = Screen.Detail(it)
            },
            onFavoritesClick = { currentScreen = Screen.Favorites }
        )
        is Screen.Favorites -> FavoritesScreen(
            viewModel = viewModel,
            onMovieClick = { movieId ->
                viewModel.getMovieById(movieId)?.let { movie ->
                    viewModel.selectMovie(movie)
                    currentScreen = Screen.Detail(movie)
                }
            }
        )
        is Screen.Detail -> {
            MovieDetailScreen(
                viewModel = viewModel,
                onBackPress = { 
                    viewModel.clearSelectedMovie()
                    currentScreen = Screen.List
                }
            )
        }
    }
}