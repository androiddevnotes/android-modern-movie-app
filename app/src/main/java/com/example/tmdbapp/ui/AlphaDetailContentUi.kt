package com.example.tmdbapp.ui

import android.content.Context
import android.graphics.Color.TRANSPARENT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.tmdbapp.models.AiResponseUiState
import com.example.tmdbapp.ui.components.AiResponseCardUi
import com.example.tmdbapp.utils.Constants
import kotlinx.coroutines.launch

@Composable
fun <T : Any> AlphaDetailContentUi(
  item: T,
  onBackPress: () -> Unit,
  onFavoriteClick: () -> Unit,
  onDownloadClick: (String?, Context) -> Unit,
  onAskAiClick: () -> Unit,
  aiResponseUiState: AiResponseUiState<String>,
  getItemTitle: (T) -> String,
  getItemOverview: (T) -> String,
  getItemPosterPath: (T) -> String?,
  getItemReleaseDate: (T) -> String?,
  getItemVoteAverage: (T) -> Float,
  isItemFavorite: (T) -> Boolean,
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  val coroutineScope = rememberCoroutineScope()

  var dominantColor by remember { mutableStateOf(Color.Transparent) }
  var textColor by remember { mutableStateOf(Color.White) }

  LaunchedEffect(getItemPosterPath(item)) {
    coroutineScope.launch {
      getItemPosterPath(item)?.let { posterPath ->
        val imageUrl = "${Constants.BASE_IMAGE_URL}$posterPath"
        val loader = ImageLoader(context)
        val request =
          ImageRequest
            .Builder(context)
            .data(imageUrl)
            .allowHardware(false)
            .build()

        val result = (loader.execute(request) as? SuccessResult)?.drawable
        result?.let {
          val bitmap = it.toBitmap()
          val palette = Palette.from(bitmap).generate()
          dominantColor = Color(palette.getDominantColor(TRANSPARENT))
          textColor = if (dominantColor.luminance() > 0.5f) Color.Black else Color.White
        }
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize()) {
    AlphaDetailBackgroundImageUi(getItemPosterPath(item))
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .verticalScroll(scrollState)
          .padding(bottom = 80.dp),
    ) {
      AlphaDetailTopBarUi(
        onBackPress = onBackPress,
        onFavoriteClick = onFavoriteClick,
        onDownloadClick = { onDownloadClick(getItemPosterPath(item), context) },
        onAskAiClick = onAskAiClick,
        isFavorite = isItemFavorite(item),
        textColor = textColor,
      )
      Spacer(modifier = Modifier.weight(1f))
      GenericDetailInfo(
        title = getItemTitle(item),
        overview = getItemOverview(item),
        releaseDate = getItemReleaseDate(item),
        voteAverage = getItemVoteAverage(item),
        textColor = textColor,
      )

      Spacer(modifier = Modifier.height(16.dp))
      when (aiResponseUiState) {
        is AiResponseUiState.Loading -> {
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

        is AiResponseUiState.Error -> {
          Text(
            text = aiResponseUiState.message,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp),
          )
        }

        is AiResponseUiState.Success -> {
          AiResponseCardUi(response = aiResponseUiState.data)
        }

        AiResponseUiState.Idle -> {
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
  textColor: Color,
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
      color = textColor,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    releaseDate?.let { date ->
      Text(
        text = "Release Date: $date",
        style = MaterialTheme.typography.bodyMedium,
        color = textColor.copy(alpha = 0.7f),
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
    Text(
      text = "Rating: $voteAverage",
      style = MaterialTheme.typography.bodyMedium,
      color = textColor.copy(alpha = 0.7f),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "Overview",
      style = MaterialTheme.typography.titleMedium,
      color = textColor,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = overview,
      style = MaterialTheme.typography.bodyMedium,
      color = textColor.copy(alpha = 0.9f),
    )
  }
}
