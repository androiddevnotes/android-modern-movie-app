package com.example.tmdbapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.viewmodel.MovieViewModel
import com.example.tmdbapp.ui.components.MovieItem

@Composable
fun FavoritesScreen(
    viewModel: MovieViewModel,
    onMovieClick: (Int) -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()

    if (favorites.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No favorites yet")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favorites) { movie ->
                MovieItem(
                    movie = movie,
                    modifier = Modifier.clickable { onMovieClick(movie.id) },
                    onFavoriteClick = { viewModel.toggleFavorite(movie) }
                )
            }
        }
    }
}