package com.example.tmdbapp.ui

import android.app.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.tmdbapp.ui.theme.*
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.viewmodel.*

@Composable
fun NavGraph(
  navController: NavHostController,
  itemViewModel: ItemViewModel,
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
      ItemListScreenUi(
        itemViewModel = itemViewModel,
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
        itemViewModel.fetchMovieDetails(movieId)
      }
      ItemDetailScreenUi(
        itemViewModel = itemViewModel,
        onBackPress = { navController.popBackStack() },
      )
    }
    composable("favorites") {
      ItemListFavoriteScreenUi(
        viewModel = itemViewModel,
        onItemClick = { movieId ->
          navController.navigate("movieDetail/$movieId")
        },
        onBackPress = { navController.popBackStack() },
      )
    }
    composable("createList") {
      ItemListCreationScreen(
        itemViewModel = itemViewModel,
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
