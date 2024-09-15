//package com.example.tmdbapp.ui
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.navigation.compose.rememberNavController
//import com.example.tmdbapp.ui.theme.TMDBAppTheme
//import com.example.tmdbapp.ui.theme.ThemeMode
//import com.example.tmdbapp.utils.Constants
//import com.example.tmdbapp.viewmodel.MovieViewModel
//
//@Composable
//fun AppContent(
//    movieViewModel: MovieViewModel,
//    initialThemeMode: ThemeMode
//) {
//    var themeMode by rememberSaveable { mutableStateOf(initialThemeMode) }
//    var viewType by rememberSaveable { mutableStateOf(Constants.VIEW_TYPE_GRID) }
//    val navController = rememberNavController()
//
//    TMDBAppTheme(themeMode = themeMode) {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = MaterialTheme.colorScheme.background
//        ) {
//            NavGraph(
//                navController = navController,
//                movieViewModel = movieViewModel,
//                currentThemeMode = themeMode,
//                onThemeChange = {
//                    themeMode = when (themeMode) {
//                        ThemeMode.LIGHT -> ThemeMode.DARK
//                        ThemeMode.DARK -> ThemeMode.SYSTEM
//                        ThemeMode.SYSTEM -> ThemeMode.LIGHT
//                    }
//                },
//                viewType = viewType,
//                onViewTypeChange = { newViewType ->
//                    viewType = newViewType
//                }
//            )
//        }
//    }
//}