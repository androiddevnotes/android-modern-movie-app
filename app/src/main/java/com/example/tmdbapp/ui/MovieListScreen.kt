package com.example.tmdbapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.viewmodel.MovieUiState
import com.example.tmdbapp.viewmodel.MovieViewModel
import com.example.tmdbapp.ui.components.MovieItem
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MovieListScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Movie) -> Unit,
    onFavoritesClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollPosition by viewModel.listScrollPosition.collectAsState()
    
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = scrollPosition.firstVisibleItemIndex,
        initialFirstVisibleItemScrollOffset = scrollPosition.firstVisibleItemScrollOffset
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .distinctUntilChanged()
            .collect { (index, offset) ->
                viewModel.saveListScrollPosition(index, offset)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Popular Movies") },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                is MovieUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is MovieUiState.Success -> {
                    val movies = (uiState as MovieUiState.Success).movies
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(movies) { index, movie ->
                            if (index >= movies.size - 1) {
                                viewModel.loadMoreMovies()
                            }
                            MovieItem(
                                movie = movie,
                                modifier = Modifier.clickable {
                                    viewModel.selectMovie(movie)
                                    onMovieClick(movie)
                                },
                                onFavoriteClick = { viewModel.toggleFavorite(movie) }
                            )
                        }
                        item {
                            if (movies.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
                is MovieUiState.Error -> {
                    val message = (uiState as MovieUiState.Error).message
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = message)
                    }
                }
            }
        }
    }
}