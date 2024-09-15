package com.example.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.tmdbapp.ui.MainScreen
import com.example.tmdbapp.ui.theme.TMDBAppTheme
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            
            TMDBAppTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val movieViewModel: MovieViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(MovieViewModel::class.java)
                    MainScreen(
                        movieViewModel = movieViewModel,
                        onThemeChange = { newThemeMode -> themeMode = newThemeMode }
                    )
                }
            }
        }
    }
}