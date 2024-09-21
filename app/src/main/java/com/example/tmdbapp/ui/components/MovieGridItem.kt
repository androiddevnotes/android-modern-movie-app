package com.example.tmdbapp.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.*
import coil.request.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.utils.*

@Composable
fun MovieGridItem(
  movie: Movie,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  isFavorite: Boolean,
) {
  val context = LocalContext.current
  val imageRequest =
    remember(movie.posterPath) {
      ImageRequest
        .Builder(context)
        .data(Constants.BASE_IMAGE_URL + movie.posterPath)
        .crossfade(true)
        .build()
    }

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
      AsyncImage(
        model = imageRequest,
        contentDescription = movie.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
      )
      Box(
        modifier =
          Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                startY = 100f,
              ),
            ),
      )
      Column(
        modifier =
          Modifier
            .align(Alignment.BottomStart)
            .padding(8.dp),
      ) {
        Text(
          text = movie.title,
          style = MaterialTheme.typography.bodyMedium,
          color = Color.White,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = Color.Yellow,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = String.format("%.1f", movie.voteAverage),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
          )
        }
      }
      if (isFavorite) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = "Favorite",
          tint = MaterialTheme.colorScheme.primary,
          modifier =
            Modifier
              .align(Alignment.TopEnd)
              .padding(8.dp)
              .size(24.dp),
        )
      }

      // Add ShimmeringOverlay
      ShimmeringOverlay(isVisible = true)
    }
  }
}
