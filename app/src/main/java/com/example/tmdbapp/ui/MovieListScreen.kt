package com.example.tmdbapp.ui


import FilterBottomSheet
import MovieGridItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.R
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.MovieItem
import com.example.tmdbapp.ui.components.TopBar
import com.example.tmdbapp.ui.theme.ThemeMode
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.utils.rememberForeverLazyListState
import com.example.tmdbapp.utils.rememberForeverLazyStaggeredGridState
import com.example.tmdbapp.viewmodel.MovieUiState
import com.example.tmdbapp.viewmodel.MovieViewModel
import com.example.tmdbapp.viewmodel.SortOption
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Movie) -> Unit,
    onFavoritesClick: () -> Unit,
    screenTitle: String,
    viewType: String,
    onViewTypeChange: (String) -> Unit,
    onThemeChange: () -> Unit,
    currentThemeMode: ThemeMode
) {
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var showFilterBottomSheet by rememberSaveable { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    var expandedDropdown by remember { mutableStateOf(false) }
    val currentSortOption by viewModel.currentSortOption.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                viewModel.refreshMovies()
                isRefreshing = false
            }
        }
    )

    val searchQuery by viewModel.searchQuery.collectAsState()

    val lastViewedItemIndex by viewModel.lastViewedItemIndex.collectAsState()

    val listState = rememberForeverLazyListState(
        key = "movie_list_${viewType}_${searchQuery}",
        initialFirstVisibleItemIndex = lastViewedItemIndex,
        initialFirstVisibleItemScrollOffset = 0 // You can adjust this value if needed
    )
    val gridState = rememberForeverLazyStaggeredGridState(
        key = "movie_grid_${viewType}_${searchQuery}",
        initialFirstVisibleItemIndex = lastViewedItemIndex,
        initialFirstVisibleItemOffset = 0 // You can adjust this value if needed
    )

    val currentFilters by viewModel.filterOptions.collectAsState()

    if (showFilterBottomSheet) {
        FilterBottomSheet(
            currentFilters = currentFilters,
            onDismiss = { showFilterBottomSheet = false },
            onApply = { newFilters ->
                viewModel.setFilterOptions(newFilters)
            }
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
                screenTitle = screenTitle,
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
                onFilterClick = { showFilterBottomSheet = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullRefresh(pullRefreshState)
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
                                state = gridState
                            ) {
                                itemsIndexed(
                                    items = movies,
                                    key = { index, movie -> "${movie.id}_${index}" }
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
                                        isFavorite = viewModel.isFavorite(movie.id)
                                    )
                                }
                            }
                        }

                        Constants.VIEW_TYPE_LIST -> {
                            LazyColumn(
                                contentPadding = PaddingValues(Constants.PADDING_MEDIUM),
                                verticalArrangement = Arrangement.spacedBy(Constants.PADDING_MEDIUM),
                                state = listState
                            ) {
                                itemsIndexed(
                                    items = movies,
                                    key = { index, movie -> "${movie.id}_${index}" } // Use index to ensure uniqueness
                                ) { index, movie ->
                                    if (index >= movies.size - 1 && !viewModel.isLastPage) {
                                        viewModel.loadMoreMovies()
                                    }
                                    MovieItem(
                                        movie = movie,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.setLastViewedItemIndex(index)
                                                onMovieClick(movie)
                                            },
                                        onFavoriteClick = { viewModel.toggleFavorite(movie) },
                                        isListView = true
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
                                text = stringResource(error.messageResId),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
                            Button(onClick = { viewModel.loadMoreMovies() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}