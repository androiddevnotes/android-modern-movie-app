package com.example.tmdbapp.ui.nav

import android.app.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.tmdbapp.ui.screens.*
import com.example.tmdbapp.ui.theme.*
import com.example.tmdbapp.ui.viewmodel.TmdbViewModel
import com.example.tmdbapp.ui.viewmodel.fetchMovieDetails
import com.example.tmdbapp.utils.ApiKeyManager
import org.koin.androidx.compose.get

@Composable
fun NavGraph(
  navController: NavHostController,
  tmdbViewModel: TmdbViewModel,
  currentThemeMode: ThemeMode,
  onThemeChange: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  application: Application,
  modifier: Modifier = Modifier,
) {
  val apiKeyManager = get<ApiKeyManager>()

  NavHost(navController = navController, startDestination = "movieList", modifier = modifier) {
    composable("movieList") {
      TmdbListScreenUi(
        tmdbViewModel = tmdbViewModel,
        onItemClick = { movie ->
          navController.navigate("movieDetail/${movie.id}")
        },
        onFavoritesClick = {
          navController.navigate("favorites") {
            popUpTo(navController.graph.findStartDestination().id) {
              saveState = true
            }
            launchSingleTop = true
            restoreState = true
          }
        },
        viewType = viewType,
        onViewTypeChange = onViewTypeChange,
        onThemeChange = onThemeChange,
        currentThemeMode = currentThemeMode,
        onSettingsClick = {
          navController.navigate("settings")
        },
      )
    }
    composable(
      "movieDetail/{movieId}",
      arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
    ) { backStackEntry ->
      val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
      LaunchedEffect(movieId) {
        tmdbViewModel.fetchMovieDetails(movieId)
      }
      TmdbDetailScreenUi(
        tmdbViewModel = tmdbViewModel,
        onBackPress = { navController.popBackStack() },
      )
    }
    composable("favorites") {
      TmdbListFavoriteScreenUi(
        viewModel = tmdbViewModel,
        onItemClick = { movieId ->
          navController.navigate("movieDetail/$movieId")
        },
        onBackPress = { navController.popBackStack() },
      )
    }
    composable("createList") {
      TmdbCreateListScreenUi(
        tmdbViewModel = tmdbViewModel,
        onNavigateBack = { navController.popBackStack() },
        application = application,
      )
    }

    composable("settings") {
      SettingsScreenUi(
        apiKeyManager = apiKeyManager,
        onBackPress = { navController.popBackStack() },
      )
    }
  }
}
