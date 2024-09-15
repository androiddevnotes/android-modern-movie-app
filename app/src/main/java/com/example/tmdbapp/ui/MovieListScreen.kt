package com.example.tmdbapp.ui


import FilterBottomSheet
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
    currentThemeMode: ThemeMode,
    onDummyListClick: () -> Unit
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
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            stringResource(R.string.label_search_movies),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            isSearchActive = false
                            viewModel.setSearchQuery("")
                            // Don't reset scroll position here
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = true
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            screenTitle,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Image(
                                painter = painterResource(id = R.drawable.cool_shape_search),
                                contentDescription = stringResource(R.string.content_desc_search),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = { expandedDropdown = true }) {
                            Image(
                                painter = painterResource(id = R.drawable.cool_shape_sort),
                                contentDescription = stringResource(R.string.content_desc_sort),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            SortOption.values().forEach { sortOption ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(sortOption.stringRes)) },
                                    onClick = {
                                        viewModel.setSortOption(sortOption)
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                        IconButton(onClick = onFavoritesClick) {
                            Image(
                                painter = painterResource(id = R.drawable.cool_shape_fav),
                                contentDescription = stringResource(R.string.content_desc_favorites),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = {
                            onViewTypeChange(
                                if (viewType == Constants.VIEW_TYPE_GRID) Constants.VIEW_TYPE_LIST
                                else Constants.VIEW_TYPE_GRID
                            )
                        }) {
                            Image(
                                painter = painterResource(
                                    id = if (viewType == Constants.VIEW_TYPE_GRID) R.drawable.cool_shape_list
                                    else R.drawable.cool_shape_grid
                                ),
                                contentDescription = stringResource(R.string.content_desc_switch_view),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = onThemeChange) {
                            Image(
                                painter = painterResource(
                                    id = when (currentThemeMode) {
                                        ThemeMode.LIGHT -> R.drawable.cool_shape_night
                                        ThemeMode.DARK -> R.drawable.cool_shape_light
                                        ThemeMode.SYSTEM -> R.drawable.cool_shape_theme_system
                                    }
                                ),
                                contentDescription = stringResource(R.string.content_desc_toggle_theme),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = { showFilterBottomSheet = true }) {
                            Image(
                                painter = painterResource(id = R.drawable.cool_shape_filter),
                                contentDescription = stringResource(R.string.content_desc_filter),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = onDummyListClick) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = stringResource(R.string.content_desc_dummy_list)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
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
                                columns = StaggeredGridCells.Adaptive(Constants.GRID_COLUMNS_ADAPTIVE_WIDTH.dp),
                                contentPadding = PaddingValues(Constants.PADDING_MEDIUM),
                                horizontalArrangement = Arrangement.spacedBy(Constants.PADDING_MEDIUM),
                                verticalItemSpacing = Constants.PADDING_MEDIUM,
                                state = gridState
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
                                        modifier = Modifier.clickable {
                                            viewModel.setLastViewedItemIndex(index)
                                            onMovieClick(movie)
                                        },
                                        onFavoriteClick = { viewModel.toggleFavorite(movie) },
                                        isListView = false
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