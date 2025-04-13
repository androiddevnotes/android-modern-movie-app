package com.example.tmdbapp.ui.screens

import androidx.compose.runtime.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.TmdbListContentUi
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.ui.viewmodel.TmdbViewModel
import com.example.tmdbapp.utils.rememberForeverLazyListState
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState

@Composable
fun TmdbListScreenUi(
    tmdbViewModel: TmdbViewModel,
    onItemClick: (Movie) -> Unit,
    onFavoritesClick: () -> Unit,
    viewType: String,
    onViewTypeChange: (String) -> Unit,
    onThemeChange: () -> Unit,
    currentThemeMode: ThemeMode,
    onSettingsClick: () -> Unit,
) {
  val uiState by tmdbViewModel.tmdbListUiState.collectAsState()
  val searchQuery by tmdbViewModel.searchQuery.collectAsState()
  val currentSortOption by tmdbViewModel.currentSortOptions.collectAsState()
  val currentFilters by tmdbViewModel.filterOptions.collectAsState()

  val listState = rememberForeverLazyListState(key = "item_list_${viewType}_$searchQuery")
  val gridState = rememberForeverLazyStaggeredGridState(key = "item_grid_${viewType}_$searchQuery")

  LaunchedEffect(viewType) {
    tmdbViewModel.clearScrollToIndex()
  }

  TmdbListContentUi(
    tmdbListUiState = uiState,
    searchQuery = searchQuery,
    currentSortOptions = currentSortOption,
    currentFilters = currentFilters,
    viewType = viewType,
    onItemClick = onItemClick,
    onFavoritesClick = onFavoritesClick,
    onViewTypeChange = onViewTypeChange,
    onThemeChange = onThemeChange,
    currentThemeMode = currentThemeMode,
    onSettingsClick = onSettingsClick,
    listState = listState,
    gridState = gridState,
    getItemId = { it.id },
    getItemTitle = { it.title },
    getItemOverview = { it.overview },
    getItemPosterPath = { it.posterPath },
    getItemVoteAverage = { it.voteAverage },
    isItemFavorite = { it.isFavorite },
    toggleFavorite = tmdbViewModel::toggleFavorite,
    isLastPage = tmdbViewModel.isLastPage,
    loadMoreItems = tmdbViewModel::loadMoreItems,
    refreshItems = tmdbViewModel::refreshItems,
    setLastViewedItemIndex = tmdbViewModel::setLastViewedItemIndex,
    setSearchQuery = tmdbViewModel::setSearchQuery,
    setSortOption = tmdbViewModel::setSortOption,
    setFilterOptions = tmdbViewModel::setFilterOptions,
  )
}
