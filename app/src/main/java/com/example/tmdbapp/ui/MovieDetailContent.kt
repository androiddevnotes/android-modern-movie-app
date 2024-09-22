package com.example.tmdbapp.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.ui.components.AIResponseCard
import com.example.tmdbapp.viewmodel.AIResponseState
import com.example.tmdbapp.viewmodel.AIResponseState.*

@Composable
fun MovieDetailContent(
  movie: Movie,
  onBackPress: () -> Unit,
  onFavoriteClick: () -> Unit,
  onDownloadClick: (String?, Context) -> Unit,
  onAskAIClick: () -> Unit,
  aiResponseState: AIResponseState<String>, // Change to AIResponseState<String>
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Box(modifier = Modifier.fillMaxSize()) {
    MovieBackgroundImage(movie.posterPath)
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .padding(bottom = 80.dp),
    ) {
      MovieDetailTopBar(
        onBackPress = onBackPress,
        onFavoriteClick = onFavoriteClick,
        onDownloadClick = { onDownloadClick(movie.posterPath, context) },
        onAskAIClick = onAskAIClick,
        isFavorite = movie.isFavorite,
      )
      Spacer(modifier = Modifier.weight(1f))
      MovieDetailInfo(movie)

      Spacer(
        modifier =
          Modifier
            .height(16.dp),
      )
      when (aiResponseState) {
        is AIResponseState.Loading -> {
          Box(
            modifier =
              Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
          ) {
            CircularProgressIndicator(
              modifier =
                Modifier
                  .size(24.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        is AIResponseState.Error -> {
          Text(
            text = aiResponseState.message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp),
          )
        }

        is AIResponseState.Success -> {
          AIResponseCard(response = aiResponseState.data)
        }

        AIResponseState.Idle -> {
          // Do nothing or show a placeholder
        }
      }
    }
  }
}
