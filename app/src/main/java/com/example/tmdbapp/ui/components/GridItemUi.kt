package com.example.tmdbapp.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.utils.Constants

@Composable
fun GridItemUi(
  title: String,
  posterPath: String?,
  voteAverage: Float,
  isFavorite: Boolean,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
) {
  val context = LocalContext.current
  val imageRequest = rememberImageRequest("${Constants.BASE_IMAGE_URL}$posterPath", context)

  Card(
    modifier =
      Modifier
        .fillMaxWidth()
        .height(200.dp)
        .combinedClickable(
          onClick = onClick,
          onLongClick = onLongClick,
        ),
    shape = RoundedCornerShape(8.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      GridItemThumbnailUi(imageRequest, title)
      GridItemInfoUi(
        title = title,
        voteAverage = voteAverage,
        modifier =
          Modifier
            .align(Alignment.BottomStart)
            .padding(8.dp),
      )
      if (isFavorite) {
        Icon(
          imageVector = Icons.Filled.Favorite,
          contentDescription = "Favorite",
          tint = MaterialTheme.colorScheme.primary,
          modifier =
            Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
              .size(24.dp),
        )
      }
    }
  }
}
