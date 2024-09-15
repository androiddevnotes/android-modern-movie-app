package com.example.tmdbapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    movieViewModel: MovieViewModel,
    currentThemeMode: ThemeMode,
    onThemeChange: () -> Unit,
    viewType: String,
    onViewTypeChange: (String) -> Unit
) {
    NavHost(navController = navController, startDestination = "movieList") {
        composable("movieList") {
            MovieListScreen(
                viewModel = movieViewModel,
                onMovieClick = { movie ->
                    navController.navigate("movieDetail/${movie.id}")
                },
                onFavoritesClick = {
                    navController.navigate("favorites")
                },
                screenTitle = Constants.SCREEN_TITLE_DISCOVER,
                viewType = viewType,
                onViewTypeChange = onViewTypeChange,
                onThemeChange = onThemeChange,
                currentThemeMode = currentThemeMode,
            
            )
        }
        composable(
            "movieDetail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
            LaunchedEffect(movieId) {
                movieViewModel.fetchMovieDetails(movieId)
            }
            MovieDetailScreen(
                viewModel = movieViewModel,
                onBackPress = { navController.popBackStack() }
            )
        }
        composable("favorites") {
            FavoritesScreen(
                viewModel = movieViewModel,
                onMovieClick = { movieId ->
                    navController.navigate("movieDetail/$movieId")
                },
                onBackPress = { navController.popBackStack() }
            )
        }


    }
}

// Helper function to cycle through theme modes
private fun ThemeMode.next(): ThemeMode = when (this) {
    ThemeMode.LIGHT -> ThemeMode.DARK
    ThemeMode.DARK -> ThemeMode.SYSTEM
    ThemeMode.SYSTEM -> ThemeMode.LIGHT
}