package com.example.tmdbapp.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.viewmodel.*

@Composable
fun FavoritesScreen(
  viewModel: MovieViewModel,
  onMovieClick: (Int) -> Unit,
  onBackPress: () -> Unit,
) {
  val favorites by viewModel.favorites.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            Constants.SCREEN_TITLE_FAVORITES,
            style = typography.headlineMedium,
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackPress) {
            Icon(
              Icons.Filled.ArrowBack,
              contentDescription = Constants.CONTENT_DESC_BACK,
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surface,
            titleContentColor = colorScheme.onSurface,
          ),
      )
    },
  ) { paddingValues ->
    if (favorites.isEmpty()) {
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center,
      ) {
        Text(Constants.MESSAGE_NO_FAVORITES)
      }
    } else {
      LazyColumn(
        contentPadding = PaddingValues(Constants.PADDING_MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Constants.PADDING_SMALL),
        modifier = Modifier.padding(paddingValues),
      ) {
        items(favorites) { movie ->
          MovieItem(
            movie = movie,
            modifier = Modifier.clickable { onMovieClick(movie.id) },
            onFavoriteClick = { viewModel.toggleFavorite(movie) },
          )
        }
      }
    }
  }
}
