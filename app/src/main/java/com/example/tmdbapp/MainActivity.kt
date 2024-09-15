package com.example.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.tmdbapp.ui.AppContent
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.viewmodel.MovieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            val movieViewModel: MovieViewModel =
                ViewModelProvider.AndroidViewModelFactory(application)
                    .create(MovieViewModel::class.java)
            
            AppContent(
                movieViewModel = movieViewModel,
                themeMode = themeMode,
                onThemeChange = { newThemeMode -> themeMode = newThemeMode }
            )
        }
    }
}