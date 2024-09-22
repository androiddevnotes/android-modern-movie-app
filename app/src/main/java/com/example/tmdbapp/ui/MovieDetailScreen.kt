package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.viewmodel.*

@Composable
fun MovieDetailScreen(
  viewModel: MovieViewModel,
  onBackPress: () -> Unit,
) {
  val movieState by viewModel.detailUiState.collectAsState()
  val aiResponseState by viewModel.aiResponseUiState.collectAsState()

  DisposableEffect(Unit) {
    onDispose {
      viewModel.clearAIResponse()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (movieState) {
      is DetailUiState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }
      is DetailUiState.Success -> {
        val movie = (movieState as DetailUiState.Success<Movie>).data
        GenericDetailContent(
          item = movie,
          onBackPress = onBackPress,
          onFavoriteClick = { viewModel.toggleFavorite(movie) },
          onDownloadClick = { posterPath, context ->
            viewModel.downloadImage(posterPath, context)
          },
          onAskAIClick = { viewModel.askAIAboutMovie(movie) },
          aiResponseUiState = aiResponseState,
          getItemTitle = { it.title },
          getItemOverview = { it.overview },
          getItemPosterPath = { it.posterPath },
          getItemReleaseDate = { it.releaseDate },
          getItemVoteAverage = { it.voteAverage },
          isItemFavorite = { it.isFavorite },
        )
      }
      is DetailUiState.Error -> {
        ErrorContent(
          error = (movieState as DetailUiState.Error).error,
          onRetry = { viewModel.retryFetchMovieDetails() },
          onBackPress = onBackPress,
        )
      }
    }
    ShimmeringOverlay(
      isVisible = aiResponseState is AIResponseUiState.Loading,
    )

    if (aiResponseState is AIResponseUiState.Loading) {
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter,
      ) {
        AiScanningIndicator()
      }
    }
  }
}
