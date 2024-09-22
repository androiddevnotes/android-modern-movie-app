package com.example.tmdbapp.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.AIResponseUiState
import com.example.tmdbapp.ui.components.AiResponseCardUi

@Composable
fun <T : Any> GenericDetailContentUi(
  item: T,
  onBackPress: () -> Unit,
  onFavoriteClick: () -> Unit,
  onDownloadClick: (String?, Context) -> Unit,
  onAskAIClick: () -> Unit,
  aiResponseUiState: AIResponseUiState<String>,
  getItemTitle: (T) -> String,
  getItemOverview: (T) -> String,
  getItemPosterPath: (T) -> String?,
  getItemReleaseDate: (T) -> String?,
  getItemVoteAverage: (T) -> Float,
  isItemFavorite: (T) -> Boolean,
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  Box(modifier = Modifier.fillMaxSize()) {
    DetailBackgroundImage(getItemPosterPath(item))
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .padding(bottom = 80.dp),
    ) {
      DetailTopBarUi(
        onBackPress = onBackPress,
        onFavoriteClick = onFavoriteClick,
        onDownloadClick = { onDownloadClick(getItemPosterPath(item), context) },
        onAskAIClick = onAskAIClick,
        isFavorite = isItemFavorite(item),
      )
      Spacer(modifier = Modifier.weight(1f))
      GenericDetailInfo(
        title = getItemTitle(item),
        overview = getItemOverview(item),
        releaseDate = getItemReleaseDate(item),
        voteAverage = getItemVoteAverage(item),
      )

      Spacer(modifier = Modifier.height(16.dp))
      when (aiResponseUiState) {
        is AIResponseUiState.Loading -> {
          Box(
            modifier =
              Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }

        is AIResponseUiState.Error -> {
          Text(
            text = aiResponseUiState.message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp),
          )
        }

        is AIResponseUiState.Success -> {
          AiResponseCardUi(response = aiResponseUiState.data)
        }

        AIResponseUiState.Idle -> {
          // Do nothing or show a placeholder
        }
      }
    }
  }
}

@Composable
private fun GenericDetailInfo(
  title: String,
  overview: String,
  releaseDate: String?,
  voteAverage: Float,
) {
  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(16.dp),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.headlineLarge,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    releaseDate?.let { date ->
      Text(
        text = "Release Date: $date",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
    Text(
      text = "Rating: $voteAverage",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "Overview",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurface,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = overview,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}