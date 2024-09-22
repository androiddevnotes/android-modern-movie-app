package com.example.tmdbapp.ui.nav

import android.app.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.tmdbapp.ui.screens.*
import com.example.tmdbapp.ui.theme.*
import com.example.tmdbapp.ui.viewmodel.AlphaViewModel
import com.example.tmdbapp.utils.ApiKeyManager

@Composable
fun NavGraph(
  navController: NavHostController,
  alphaViewModel: AlphaViewModel,
  currentThemeMode: ThemeMode,
  onThemeChange: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  application: Application,
  modifier: Modifier = Modifier,
) {
  val apiKeyManager = remember { ApiKeyManager(application) }

  NavHost(navController = navController, startDestination = "movieList", modifier = modifier) {
    composable("movieList") {
      AlphaListScreenUi(
        alphaViewModel = alphaViewModel,
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
        alphaViewModel.fetchMovieDetails(movieId)
      }
      AlphaDetailScreenUi(
        alphaViewModel = alphaViewModel,
        onBackPress = { navController.popBackStack() },
      )
    }
    composable("favorites") {
      AlphaListFavoriteScreenUi(
        viewModel = alphaViewModel,
        onItemClick = { movieId ->
          navController.navigate("movieDetail/$movieId")
        },
        onBackPress = { navController.popBackStack() },
      )
    }
    composable("createList") {
      AlphaCreateListScreenUi(
        alphaViewModel = alphaViewModel,
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
