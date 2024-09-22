package com.example.tmdbapp.ui

import androidx.compose.runtime.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.rememberForeverLazyListState
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun MovieListScreenUi(
  movieViewModel: MovieViewModel,
  onItemClick: (Movie) -> Unit,
  onFavoritesClick: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onSettingsClick: () -> Unit,
) {
  val uiState by movieViewModel.listUiState.collectAsState()
  val searchQuery by movieViewModel.searchQuery.collectAsState()
  val currentSortOption by movieViewModel.currentSortOptions.collectAsState()
  val currentFilters by movieViewModel.filterOptions.collectAsState()

  val listState = rememberForeverLazyListState(key = "item_list_${viewType}_$searchQuery")
  val gridState = rememberForeverLazyStaggeredGridState(key = "item_grid_${viewType}_$searchQuery")

  LaunchedEffect(viewType) {
    movieViewModel.clearScrollToIndex()
  }

  GenericListContentUi(
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
    toggleFavorite = movieViewModel::toggleFavorite,
    isLastPage = movieViewModel.isLastPage,
    loadMoreItems = movieViewModel::loadMoreMovies,
    refreshItems = movieViewModel::refreshMovies,
    setLastViewedItemIndex = movieViewModel::setLastViewedItemIndex,
    setSearchQuery = movieViewModel::setSearchQuery,
    setSortOption = movieViewModel::setSortOption,
    setFilterOptions = movieViewModel::setFilterOptions,
  )
}
