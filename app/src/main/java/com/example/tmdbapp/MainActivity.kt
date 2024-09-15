package com.example.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.lifecycle.ViewModelProvider
import com.example.tmdbapp.ui.FavoritesScreen
import com.example.tmdbapp.ui.MovieDetailScreen
import com.example.tmdbapp.ui.MovieListScreen
import com.example.tmdbapp.ui.theme.TMDBAppTheme
import com.example.tmdbapp.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TMDBAppTheme {
                val movieViewModel: MovieViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(application))
                val selectedMovie by movieViewModel.selectedMovie.collectAsState()
                var currentScreen by remember { mutableStateOf("list") }

                // Handle back press
                val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                DisposableEffect(backDispatcher) {
                    val callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            if (selectedMovie != null) {
                                movieViewModel.clearSelectedMovie()
                            } else {
                                isEnabled = false
                                backDispatcher?.onBackPressed()
                            }
                        }
                    }
                    backDispatcher?.addCallback(callback)
                    onDispose {
                        callback.remove()
                    }
                }

                when (currentScreen) {
                    "list" -> MovieListScreen(
                        viewModel = movieViewModel,
                        onMovieClick = { movieViewModel.selectMovie(it) },
                        onFavoritesClick = { currentScreen = "favorites" }
                    )
                    "favorites" -> FavoritesScreen(
                        viewModel = movieViewModel,
                        onMovieClick = { movieId ->
                            movieViewModel.getMovieById(movieId)
                                ?.let { movieViewModel.selectMovie(it) }
                            currentScreen = "detail"
                        }
                    )
                    "detail" -> selectedMovie?.let { movie ->
                        MovieDetailScreen(
                            movie = movie,
                            onBackPress = { 
                                movieViewModel.clearSelectedMovie()
                                currentScreen = "list"
                            },
                            onFavoriteClick = { movieViewModel.toggleFavorite(movie) }
                        )
                    }
                }
            }
        }
    }
}