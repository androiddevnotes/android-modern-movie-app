package com.example.tmdbapp.ui.screens

import androidx.compose.runtime.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.AlphaListContentUi
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.ui.viewmodel.AlphaViewModel
import com.example.tmdbapp.utils.rememberForeverLazyListState
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState

@Composable
fun AlphaListScreenUi(
  alphaViewModel: AlphaViewModel,
  onItemClick: (Movie) -> Unit,
  onFavoritesClick: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onSettingsClick: () -> Unit,
) {
  val uiState by alphaViewModel.alphaListUiState.collectAsState()
  val searchQuery by alphaViewModel.searchQuery.collectAsState()
  val currentSortOption by alphaViewModel.currentSortOptions.collectAsState()
  val currentFilters by alphaViewModel.filterOptions.collectAsState()

  val listState = rememberForeverLazyListState(key = "item_list_${viewType}_$searchQuery")
  val gridState = rememberForeverLazyStaggeredGridState(key = "item_grid_${viewType}_$searchQuery")

  LaunchedEffect(viewType) {
    alphaViewModel.clearScrollToIndex()
  }

  AlphaListContentUi(
    alphaListUiState = uiState,
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
    toggleFavorite = alphaViewModel::toggleFavorite,
    isLastPage = alphaViewModel.isLastPage,
    loadMoreItems = alphaViewModel::loadMoreItems,
    refreshItems = alphaViewModel::refreshItems,
    setLastViewedItemIndex = alphaViewModel::setLastViewedItemIndex,
    setSearchQuery = alphaViewModel::setSearchQuery,
    setSortOption = alphaViewModel::setSortOption,
    setFilterOptions = alphaViewModel::setFilterOptions,
  )
}
