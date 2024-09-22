package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.viewmodel.MovieViewModel
import com.example.tmdbapp.viewmodel.downloadImage

@Composable
fun MovieDetailScreenUi(
  movieViewModel: MovieViewModel,
  onBackPress: () -> Unit,
) {
  val detailUiState by movieViewModel.detailUiState.collectAsState()
  val aiResponseState by movieViewModel.aiResponseUiState.collectAsState()

  DisposableEffect(Unit) {
    onDispose {
      movieViewModel.clearAIResponse()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (detailUiState) {
      is DetailUiState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }

      is DetailUiState.Success -> {
        val item = (detailUiState as DetailUiState.Success<Movie>).data
        GenericDetailContentUi(
          item = item,
          onBackPress = onBackPress,
          onFavoriteClick = { movieViewModel.toggleFavorite(item) },
          onDownloadClick = { posterPath, context ->
            movieViewModel.downloadImage(posterPath, context)
          },
          onAskAIClick = { movieViewModel.askAIAboutItem(item) },
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
        ErrorContentUi(
          error = (detailUiState as DetailUiState.Error).error,
          onRetry = { movieViewModel.retryFetchItemDetails() },
          onBackPress = onBackPress,
        )
      }
    }
    ShimmeringOverlayUi(
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
        AiScanningIndicatorUi()
      }
    }
  }
}
