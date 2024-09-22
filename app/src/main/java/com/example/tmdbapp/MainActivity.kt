package com.example.tmdbapp

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.lifecycle.*
import androidx.navigation.compose.*
import com.example.tmdbapp.ui.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.ui.theme.*
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.viewmodel.*

class MainActivity : ComponentActivity() {
  private lateinit var itemViewModel: ItemViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    itemViewModel =
      ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[ItemViewModel::class.java]

    setContent {
      var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM) }
      var viewType by rememberSaveable { mutableStateOf(Constants.VIEW_TYPE_GRID) }

      TMDBAppTheme(themeMode = themeMode) {
        val navController = rememberNavController()

        Scaffold(
          bottomBar = { BottomNavigationBar(navController = navController) },
        ) { innerPadding ->
          NavGraph(
            navController = navController,
            itemViewModel = itemViewModel,
            currentThemeMode = themeMode,
            onThemeChange = { themeMode = themeMode.next() },
            viewType = viewType,
            onViewTypeChange = { newViewType -> viewType = newViewType },
            application = application,
            modifier = Modifier.padding(innerPadding),
          )
        }
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
