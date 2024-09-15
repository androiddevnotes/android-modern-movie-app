package com.example.tmdbapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.lifecycle.ViewModelProvider
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.FavoritesScreen
import com.example.tmdbapp.ui.MovieDetailScreen
import com.example.tmdbapp.ui.MovieListScreen
import com.example.tmdbapp.ui.theme.TMDBAppTheme
import com.example.tmdbapp.viewmodel.MovieViewModel

// Define a sealed class for screens
sealed class Screen {
    object List : Screen()
    object Favorites : Screen()
    data class Detail(val movie: Movie) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TMDBAppTheme {
                val movieViewModel: MovieViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(application))
                val selectedMovie by movieViewModel.selectedMovie.collectAsState()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }

                // Handle back press
                val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                LaunchedEffect(backDispatcher, currentScreen) {
                    val callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            when (currentScreen) {
                                is Screen.Detail -> {
                                    movieViewModel.clearSelectedMovie()
                                    currentScreen = Screen.List
                                }
                                is Screen.Favorites -> {
                                    currentScreen = Screen.List
                                }
                                is Screen.List -> {
                                    isEnabled = false
                                    backDispatcher?.onBackPressed()
                                }
                            }
                        }
                    }
                    backDispatcher?.addCallback(callback)
                }

                when (currentScreen) {
                    is Screen.List -> MovieListScreen(
                        viewModel = movieViewModel,
                        onMovieClick = { 
                            movieViewModel.selectMovie(it) 
                            currentScreen = Screen.Detail(it)
                        },
                        onFavoritesClick = { currentScreen = Screen.Favorites }
                    )
                    is Screen.Favorites -> FavoritesScreen(
                        viewModel = movieViewModel,
                        onMovieClick = { movieId ->
                            movieViewModel.getMovieById(movieId)?.let { movie ->
                                movieViewModel.selectMovie(movie)
                                currentScreen = Screen.Detail(movie)
                            } ?: run {
                                // Handle the null case, e.g., show a toast or navigate back
                                // Example using a simple log (replace with your preferred method)
                                Log.e("MainActivity", "Movie not found for ID: $movieId")
                            }
                        }
                    )
                    is Screen.Detail -> {
                        MovieDetailScreen(
                            movie = (currentScreen as Screen.Detail).movie,
                            onBackPress = { currentScreen = Screen.List },
                            onFavoriteClick = { movieViewModel.toggleFavorite((currentScreen as Screen.Detail).movie) }
                        )
                    }
                }
            }
        }
    }
}