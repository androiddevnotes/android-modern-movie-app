package com.example.tmdbapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tmdbapp.viewmodel.MovieViewModel
import com.example.tmdbapp.ui.theme.ThemeMode
import androidx.navigation.compose.rememberNavController

@Composable
fun NavGraph(
    navController: NavHostController,
    movieViewModel: MovieViewModel,
    currentThemeMode: ThemeMode,
    onThemeChange: () -> Unit
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
                screenTitle = "Discover",
                viewType = "grid", // You might want to make this dynamic
                onViewTypeChange = { /* Implement view type change */ },
                onThemeChange = onThemeChange,
                currentThemeMode = currentThemeMode
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