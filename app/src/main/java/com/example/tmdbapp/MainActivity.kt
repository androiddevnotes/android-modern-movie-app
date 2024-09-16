package com.example.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.tmdbapp.ui.NavGraph
import com.example.tmdbapp.ui.theme.TMDBAppTheme
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
  private lateinit var movieViewModel: MovieViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    movieViewModel =
      ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
        .get(MovieViewModel::class.java)

    setContent {
      var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
      var viewType by rememberSaveable { mutableStateOf(Constants.VIEW_TYPE_GRID) }

      TMDBAppTheme(themeMode = themeMode) {
        val navController = rememberNavController()

        NavGraph(
          navController = navController,
          movieViewModel = movieViewModel,
          currentThemeMode = themeMode,
          onThemeChange = { themeMode = themeMode.next() },
          viewType = viewType,
          onViewTypeChange = { newViewType -> viewType = newViewType },
          application = application,
        )
      }
    }
  }

  private fun ThemeMode.next(): ThemeMode =
    when (this) {
      ThemeMode.LIGHT -> ThemeMode.DARK
      ThemeMode.DARK -> ThemeMode.SYSTEM
      ThemeMode.SYSTEM -> ThemeMode.LIGHT
    }
}
