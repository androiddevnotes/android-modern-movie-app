package com.example.tmdbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.tmdbapp.ui.AppContent
import com.example.tmdbapp.ui.theme.ThemeMode
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

            AppContent(
                movieViewModel = movieViewModel,
                initialThemeMode = themeMode
            )
        }
    }
}