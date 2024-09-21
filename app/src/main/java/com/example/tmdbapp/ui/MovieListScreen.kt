package com.example.tmdbapp.ui

import MovieGridItem
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material.pullrefresh.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.ui.theme.*
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.viewmodel.*
import kotlinx.coroutines.*

@Composable
fun MovieListScreen(
  viewModel: MovieViewModel,
  onMovieClick: (Movie) -> Unit,
  onFavoritesClick: () -> Unit,
  viewType: String,
  onViewTypeChange: (String) -> Unit,
  onThemeChange: () -> Unit,
  currentThemeMode: ThemeMode,
  onCreateListClick: () -> Unit,
  onSettingsClick: () -> Unit,
) {
  var isSearchActive by rememberSaveable { mutableStateOf(false) }
  var showFilterBottomSheet by rememberSaveable { mutableStateOf(false) }

  val uiState by viewModel.uiState.collectAsState()

  var expandedDropdown by remember { mutableStateOf(false) }
  val currentSortOption by viewModel.currentSortOption.collectAsState()

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

  val searchQuery by viewModel.searchQuery.collectAsState()

  val lastViewedItemIndex by viewModel.lastViewedItemIndex.collectAsState()

  val listState =
    rememberForeverLazyListState(
      key = "movie_list_${viewType}_$searchQuery",
      initialFirstVisibleItemIndex = lastViewedItemIndex,
      initialFirstVisibleItemScrollOffset = 0,
    )
  val gridState =
    rememberForeverLazyStaggeredGridState(
      key = "movie_grid_${viewType}_$searchQuery",
      initialFirstVisibleItemIndex = lastViewedItemIndex,
      initialFirstVisibleItemOffset = 0,
    )

  val currentFilters by viewModel.filterOptions.collectAsState()

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
        onCreateListClick = onCreateListClick,
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
          val movies = (uiState as MovieUiState.Success).movies
          when (viewType) {
            Constants.VIEW_TYPE_GRID -> {
              LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalItemSpacing = 4.dp,
                state = gridState,
              ) {
                itemsIndexed(
                  items = movies,
                  key = { index, movie -> "${movie.id}_$index" },
                ) { index, movie ->
                  if (index >= movies.size - 1 && !viewModel.isLastPage) {
                    viewModel.loadMoreMovies()
                  }
                  MovieGridItem(
                    movie = movie,
                    onClick = {
                      viewModel.setLastViewedItemIndex(index)
                      onMovieClick(movie)
                    },
                    onLongClick = {
                      viewModel.toggleFavorite(movie)
                    },
                    isFavorite = viewModel.isFavorite(movie.id),
                  )
                }
              }
            }

            Constants.VIEW_TYPE_LIST -> {
              LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState,
              ) {
                itemsIndexed(
                  items = movies,
                  key = { index, movie -> "${movie.id}_$index" },
                ) { index, movie ->
                  if (index >= movies.size - 1 && !viewModel.isLastPage) {
                    viewModel.loadMoreMovies()
                  }
                  MovieItem(
                    movie = movie,
                    modifier =
                      Modifier
                        .fillMaxWidth()
                        .clickable {
                          viewModel.setLastViewedItemIndex(index)
                          onMovieClick(movie)
                        },
                    onFavoriteClick = { viewModel.toggleFavorite(movie) },
                  )
                }
              }
            }
          }
        }

        is MovieUiState.Error -> {
          val error = (uiState as MovieUiState.Error).error
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text =
                  when (error) {
                    is MovieError.ApiError -> stringResource(error.messageResId, error.errorMessage)
                    else -> stringResource(error.messageResId)
                  },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
              )
              Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
              Button(onClick = { viewModel.loadMoreMovies() }) {
                Text("Retry")
              }

              if (error is MovieError.ApiKeyMissing) {
                Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
                Button(onClick = onSettingsClick) {
                  Text("Go to Settings")
                }
              }
            }
          }
        }
      }
      PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
      )
    }
  }
}
