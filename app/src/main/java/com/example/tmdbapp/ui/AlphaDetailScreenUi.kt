package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.viewmodel.AlphaViewModel
import com.example.tmdbapp.viewmodel.downloadImage

@Composable
fun AlphaDetailScreenUi(
  alphaViewModel: AlphaViewModel,
  onBackPress: () -> Unit,
) {
  val detailUiState by alphaViewModel.itemDetailUiState.collectAsState()
  val aiResponseState by alphaViewModel.aiResponseUiState.collectAsState()

  DisposableEffect(Unit) {
    onDispose {
      alphaViewModel.clearAIResponse()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (detailUiState) {
      is ItemDetailUiState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }

      is ItemDetailUiState.Success -> {
        val item = (detailUiState as ItemDetailUiState.Success<Movie>).data
        AlphaDetailContentUi(
          item = item,
          onBackPress = onBackPress,
          onFavoriteClick = { alphaViewModel.toggleFavorite(item) },
          onDownloadClick = { posterPath, context ->
            alphaViewModel.downloadImage(posterPath, context)
          },
          onAskAiClick = { alphaViewModel.askAIAboutItem(item) },
          aiResponseUiState = aiResponseState,
          getItemTitle = { it.title },
          getItemOverview = { it.overview },
          getItemPosterPath = { it.posterPath },
          getItemReleaseDate = { it.releaseDate },
          getItemVoteAverage = { it.voteAverage },
          isItemFavorite = { it.isFavorite },
        )
      }

      is ItemDetailUiState.Error -> {
        ErrorContentUi(
          error = (detailUiState as ItemDetailUiState.Error).error,
          onRetry = { alphaViewModel.retryFetchItemDetails() },
          onBackPress = onBackPress,
        )
      }
    }
    ShimmeringOverlayUi(
      isVisible = aiResponseState is AiResponseUiState.Loading,
    )

    if (aiResponseState is AiResponseUiState.Loading) {
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
