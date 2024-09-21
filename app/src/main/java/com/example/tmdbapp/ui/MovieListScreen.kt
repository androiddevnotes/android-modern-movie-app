package com.example.tmdbapp.ui

import androidx.compose.runtime.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.rememberForeverLazyListState
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState
import com.example.tmdbapp.viewmodel.MovieViewModel

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

  val listState = rememberForeverLazyListState(key = "movie_list_${viewType}_$searchQuery")
  val gridState = rememberForeverLazyStaggeredGridState(key = "movie_grid_${viewType}_$searchQuery")

  LaunchedEffect(viewType) {
      viewModel.clearScrollToIndex()
  }

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
    listState = listState,
    gridState = gridState,
  )
}
