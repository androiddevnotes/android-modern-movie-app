package com.example.tmdbapp.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.models.*

@Composable
fun GridViewItem(
  movie: Movie,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  isFavorite: Boolean,
) {
  val context = LocalContext.current
  val imageRequest = rememberImageRequest(movie.posterPath, context)

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
      MoviePoster(imageRequest, movie.title)
      GradientOverlay()
      MovieInfo(
        title = movie.title,
        voteAverage = movie.voteAverage,
        modifier =
          Modifier
            .align(Alignment.BottomStart)
            .padding(8.dp),
      )
      if (isFavorite) {
        FavoriteIcon(
          modifier =
            Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp),
        )
      }
    }
  }
}
