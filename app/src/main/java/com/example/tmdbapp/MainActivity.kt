package com.example.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
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
                LaunchedEffect(backDispatcher, currentScreen) {
                    val callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            when (currentScreen) {
                                "detail" -> {
                                    movieViewModel.clearSelectedMovie()
                                    currentScreen = "list"
                                }
                                "favorites" -> {
                                    currentScreen = "list"
                                }
                                else -> {
                                    isEnabled = false
                                    backDispatcher?.onBackPressed()
                                }
                            }
                        }
                    }
                    backDispatcher?.addCallback(callback)
                }

                when (currentScreen) {
                    "list" -> MovieListScreen(
                        viewModel = movieViewModel,
                        onMovieClick = { 
                            movieViewModel.selectMovie(it) 
                            currentScreen = "detail"
                        },
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
                    "detail" -> MovieDetailScreen(
                        movie = selectedMovie ?: return@TMDBAppTheme,
                        onBackPress = { currentScreen = "list" },
                        onFavoriteClick = { movieViewModel.toggleFavorite(selectedMovie!!) }
                    )
                }
            }
        }
    }
}