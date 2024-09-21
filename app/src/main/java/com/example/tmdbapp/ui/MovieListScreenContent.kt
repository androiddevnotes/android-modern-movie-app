package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.viewmodel.*
import kotlinx.coroutines.launch

@Composable
fun MovieListScreenContent(
  uiState: MovieUiState,
  viewModel: MovieViewModel,
  searchQuery: String,
  currentSortOption: SortOption,
  currentFilters: FilterOptions,
  viewType: String,
  onMovieClick: (Movie) -> Unit,
  onFavoritesClick: () -> Unit,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onSettingsClick: () -> Unit,
  listState: LazyListState,
  gridState: LazyStaggeredGridState,
) {
  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var showFilterBottomSheet by rememberSaveable { mutableStateOf(false) }
  var expandedDropdown by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  var isRefreshing by remember { mutableStateOf(false) }

  val pullRefreshState =
    rememberPullRefreshState(
      refreshing = isRefreshing,
      onRefresh = {
        coroutineScope.launch {
          isRefreshing = true
          viewModel.refreshMovies()
          isRefreshing = false
        }
      },
    )

  if (showFilterBottomSheet) {
    FilterBottomSheet(
      currentFilters = currentFilters,
      onDismiss = { showFilterBottomSheet = false },
      onApply = { newFilters ->
        viewModel.setFilterOptions(newFilters)
      },
    )
  }

  Scaffold(
    topBar = {
      TopBar(
        isSearchActive = isSearchActive,
        searchQuery = searchQuery,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onSearchIconClick = { isSearchActive = true },
        onCloseSearchClick = {
          isSearchActive = false
          viewModel.setSearchQuery("")
        },
        expandedDropdown = expandedDropdown,
        onSortOptionClick = {
          viewModel.setSortOption(it)
          expandedDropdown = false
        },
        currentSortOption = currentSortOption,
        onDropdownExpand = { expandedDropdown = !expandedDropdown },
        onFavoritesClick = onFavoritesClick,
        onViewTypeChange = onViewTypeChange,
        viewType = viewType,
        onThemeChange = onThemeChange,
        currentThemeMode = currentThemeMode,
        onFilterClick = { showFilterBottomSheet = true },
        onSettingsClick = onSettingsClick,
      )
    },
  ) { paddingValues ->
    Box(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .pullRefresh(pullRefreshState),
    ) {
      when (uiState) {
        is MovieUiState.Loading -> {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
          }
        }

        is MovieUiState.Success -> {
          val movies = uiState.movies
          when (viewType) {
            Constants.VIEW_TYPE_GRID ->
              MovieListGridView(
                movies = movies,
                viewModel = viewModel,
                onMovieClick = onMovieClick,
                viewType = viewType,
                searchQuery = searchQuery,
                gridState = gridState,
              )
            Constants.VIEW_TYPE_LIST ->
              MovieListListView(
                movies = movies,
                viewModel = viewModel,
                onMovieClick = onMovieClick,
                viewType = viewType,
                searchQuery = searchQuery,
                listState = listState,
              )
          }
        }

        is MovieUiState.Error -> MovieListErrorView(uiState, viewModel, onSettingsClick)
      }
      PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
      )
      Box(modifier = Modifier.matchParentSize()) {
        ShimmeringOverlay(
          isVisible = isRefreshing || uiState is MovieUiState.Loading,
        )
      }
    }
  }
}
