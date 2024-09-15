package com.example.tmdbapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.FavoritesScreen
import com.example.tmdbapp.ui.MovieDetailScreen
import com.example.tmdbapp.ui.MovieListScreen
import com.example.tmdbapp.ui.theme.TMDBAppTheme
import com.example.tmdbapp.viewmodel.MovieViewModel

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
                // Use Material3 Surface
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    val movieViewModel: MovieViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory(application))
                    val selectedMovie by movieViewModel.selectedMovie.collectAsState()
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }

                    
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
                                    
                                    
                                    Log.e("MainActivity", "Movie not found for ID: $movieId")
                                }
                            }
                        )
                        is Screen.Detail -> {
                            MovieDetailScreen(
                                viewModel = movieViewModel,
                                onBackPress = { 
                                    movieViewModel.clearSelectedMovie()
                                    currentScreen = Screen.List
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}