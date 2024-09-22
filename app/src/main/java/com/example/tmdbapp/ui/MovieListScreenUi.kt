package com.example.tmdbapp.ui

import androidx.compose.runtime.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.rememberForeverLazyListState
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun MovieListScreenUi(
  viewModel: MovieViewModel,
  onMovieClick: (Movie) -> Unit,
  onFavoritesClick: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onSettingsClick: () -> Unit,
) {
  val uiState by viewModel.listUiState.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val currentSortOption by viewModel.currentSortOptions.collectAsState()
  val currentFilters by viewModel.filterOptions.collectAsState()

  val listState = rememberForeverLazyListState(key = "movie_list_${viewType}_$searchQuery")
  val gridState = rememberForeverLazyStaggeredGridState(key = "movie_grid_${viewType}_$searchQuery")

  LaunchedEffect(viewType) {
    viewModel.clearScrollToIndex()
  }

  GenericListContentUi(
    listUiState = uiState,
    searchQuery = searchQuery,
    currentSortOptions = currentSortOption,
    currentFilters = currentFilters,
    viewType = viewType,
    onItemClick = onMovieClick,
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
    toggleFavorite = viewModel::toggleFavorite,
    isLastPage = viewModel.isLastPage,
    loadMoreItems = viewModel::loadMoreMovies,
    refreshItems = viewModel::refreshMovies,
    setLastViewedItemIndex = viewModel::setLastViewedItemIndex,
    setSearchQuery = viewModel::setSearchQuery,
    setSortOption = viewModel::setSortOption,
    setFilterOptions = viewModel::setFilterOptions,
  )
}
