package com.example.tmdbapp.ui

import androidx.compose.runtime.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.rememberForeverLazyListState
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState
import com.example.tmdbapp.viewmodel.ItemViewModel

@Composable
fun ItemListScreenUi(
  itemViewModel: ItemViewModel,
  onItemClick: (Movie) -> Unit,
  onFavoritesClick: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onSettingsClick: () -> Unit,
) {
  val uiState by itemViewModel.listUiState.collectAsState()
  val searchQuery by itemViewModel.searchQuery.collectAsState()
  val currentSortOption by itemViewModel.currentSortOptions.collectAsState()
  val currentFilters by itemViewModel.filterOptions.collectAsState()

  val listState = rememberForeverLazyListState(key = "item_list_${viewType}_$searchQuery")
  val gridState = rememberForeverLazyStaggeredGridState(key = "item_grid_${viewType}_$searchQuery")

  LaunchedEffect(viewType) {
    itemViewModel.clearScrollToIndex()
  }

  ItemListContentUi(
    listUiState = uiState,
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
    toggleFavorite = itemViewModel::toggleFavorite,
    isLastPage = itemViewModel.isLastPage,
    loadMoreItems = itemViewModel::loadMoreItems,
    refreshItems = itemViewModel::refreshItems,
    setLastViewedItemIndex = itemViewModel::setLastViewedItemIndex,
    setSearchQuery = itemViewModel::setSearchQuery,
    setSortOption = itemViewModel::setSortOption,
    setFilterOptions = itemViewModel::setFilterOptions,
  )
}
