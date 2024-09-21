package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.viewmodel.*

// Define custom colors for AI elements
val AICyan = Color(0xFF00BFFF)
val AIYellow = Color(0xFFFFD700)
val AIPink = Color(0xFFFF69B4)
val AIGreen = Color(0xFF00FF7F)

@Composable
fun MovieDetailScreen(
  viewModel: MovieViewModel,
  onBackPress: () -> Unit,
) {
  val movieState by viewModel.movieDetailState.collectAsState()
  val aiResponse by viewModel.aiResponse.collectAsState()
  val aiResponseState by viewModel.aiResponseState.collectAsState()

  // Clear AI response when leaving the screen
  DisposableEffect(Unit) {
    onDispose {
      viewModel.clearAIResponse()
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    when (movieState) {
      is MovieDetailState.Loading -> {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator()
        }
      }
      is MovieDetailState.Success -> {
        val movie = (movieState as MovieDetailState.Success).movie
        MovieDetailContent(
          movie = movie,
          onBackPress = onBackPress,
          onFavoriteClick = { viewModel.toggleFavorite(movie) },
          onDownloadClick = viewModel::downloadImage,
          onAskAIClick = { viewModel.askAIAboutMovie(movie) },
          aiResponse = aiResponse,
          aiResponseState = aiResponseState,
        )
      }
      is MovieDetailState.Error -> {
        ErrorContent(
          error = (movieState as MovieDetailState.Error).error,
          onRetry = { viewModel.retryFetchMovieDetails() },
          onBackPress = onBackPress,
        )
      }
    }

    // Shimmering overlay
    ShimmeringOverlay(
      isVisible = aiResponseState == AIResponseState.Loading,
    )

    // AI scanning indicator
    if (aiResponseState == AIResponseState.Loading) {
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.BottomCenter,
      ) {
        AIScanningIndicator()
      }
    }
  }
}
