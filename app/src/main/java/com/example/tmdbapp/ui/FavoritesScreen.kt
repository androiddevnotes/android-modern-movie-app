package com.example.tmdbapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.example.tmdbapp.ui.components.SimpleItemUi
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun FavoritesScreen(
  viewModel: MovieViewModel,
  onItemClick: (Int) -> Unit,
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
              Filled.ArrowBack,
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
          SimpleItemUi(
            title = movie.title,
            overview = movie.overview,
            posterPath = movie.posterPath,
            voteAverage = movie.voteAverage,
            isFavorite = movie.isFavorite,
            modifier = Modifier.clickable { onItemClick(movie.id) },
            onFavoriteClick = { viewModel.toggleFavorite(movie) },
          )
        }
      }
    }
  }
}
