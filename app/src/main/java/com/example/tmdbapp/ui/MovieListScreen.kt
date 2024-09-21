package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.ui.theme.*
import com.example.tmdbapp.viewmodel.*

@Composable
fun MovieListScreen(
  viewModel: MovieViewModel,
  onMovieClick: (Movie) -> Unit,
  onFavoritesClick: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onSettingsClick: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val currentSortOption by viewModel.currentSortOption.collectAsState()
  val currentFilters by viewModel.filterOptions.collectAsState()

  MovieListScreenContent(
    uiState = uiState,
    viewModel = viewModel,
    searchQuery = searchQuery,
    currentSortOption = currentSortOption,
    currentFilters = currentFilters,
    viewType = viewType,
    onMovieClick = onMovieClick,
    onFavoritesClick = onFavoritesClick,
    onViewTypeChange = onViewTypeChange,
    onThemeChange = onThemeChange,
    currentThemeMode = currentThemeMode,
    onSettingsClick = onSettingsClick,
  )
}
