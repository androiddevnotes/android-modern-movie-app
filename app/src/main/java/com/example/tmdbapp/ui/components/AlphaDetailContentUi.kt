package com.example.tmdbapp.ui.components

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
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.tmdbapp.models.BetaPieceUiState
import com.example.tmdbapp.utils.Constants
import kotlinx.coroutines.launch

@Composable
fun <T : Any> AlphaDetailContentUi(
  item: T,
  onBackPress: () -> Unit,
  onFavoriteClick: () -> Unit,
  onDownloadClick: (String?, Context) -> Unit,
  onAskAiClick: () -> Unit,
  betaPieceUiState: BetaPieceUiState<String>,
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
      AlphaDetailInfoUi(
        title = getItemTitle(item),
        overview = getItemOverview(item),
        releaseDate = getItemReleaseDate(item),
        voteAverage = getItemVoteAverage(item),
        textColor = textColor,
      )

      Spacer(modifier = Modifier.height(16.dp))
      when (betaPieceUiState) {
        is BetaPieceUiState.Loading -> {
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

        is BetaPieceUiState.Error -> {
          ErrorTextUi(error = betaPieceUiState.error)
        }

        is BetaPieceUiState.Success -> {
          BetaPieceCardUi(response = betaPieceUiState.data)
        }

        BetaPieceUiState.Idle -> {
        }
      }
    }
  }
}
