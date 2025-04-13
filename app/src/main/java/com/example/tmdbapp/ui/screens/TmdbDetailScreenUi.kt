package com.example.tmdbapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.ui.components.TmdbDetailContentUi
import com.example.tmdbapp.ui.viewmodel.TmdbViewModel
import com.example.tmdbapp.ui.viewmodel.downloadImage

@Composable
fun TmdbDetailScreenUi(
    tmdbViewModel: TmdbViewModel,
    onBackPress: () -> Unit,
) {
  val detailUiState by tmdbViewModel.tmdbDetailUiState.collectAsState()
  val aiResponseState by tmdbViewModel.betaPieceUiState.collectAsState()

  DisposableEffect(Unit) {
    onDispose {
      tmdbViewModel.clearAIResponse()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (detailUiState) {
      is TmdbDetailUiState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }

      is TmdbDetailUiState.Success -> {
        val item = (detailUiState as TmdbDetailUiState.Success<Movie>).data
        TmdbDetailContentUi(
          item = item,
          onBackPress = onBackPress,
          onFavoriteClick = { tmdbViewModel.toggleFavorite(item) },
          onDownloadClick = { posterPath, context ->
            tmdbViewModel.downloadImage(posterPath, context)
          },
          onAskAiClick = { tmdbViewModel.askAIAboutItem(item) },
          betaPieceUiState = aiResponseState,
          getItemTitle = { it.title },
          getItemOverview = { it.overview },
          getItemPosterPath = { it.posterPath },
          getItemReleaseDate = { it.releaseDate },
          getItemVoteAverage = { it.voteAverage },
          isItemFavorite = { it.isFavorite },
        )
      }

      is TmdbDetailUiState.Error -> {
        ErrorContentUi(
          error = (detailUiState as TmdbDetailUiState.Error).error,
          onRetry = { tmdbViewModel.retryFetchItemDetails() },
          onBackPress = onBackPress,
        )
      }
    }
    ShimmeringOverlayUi(
      isVisible = aiResponseState is BetaPieceUiState.Loading,
    )

    if (aiResponseState is BetaPieceUiState.Loading) {
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter,
      ) {
        ScanningIndicatorUi()
      }
    }
  }
}
