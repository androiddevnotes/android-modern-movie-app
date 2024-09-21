package com.example.tmdbapp.ui.components

import android.content.Context
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
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

@Composable
private fun rememberImageRequest(
  posterPath: String?,
  context: Context,
) = remember(posterPath) {
  ImageRequest
    .Builder(context)
    .data(Constants.BASE_IMAGE_URL + posterPath)
    .crossfade(true)
    .build()
}

@Composable
private fun MoviePoster(
  imageRequest: ImageRequest,
  title: String,
) {
  AsyncImage(
    model = imageRequest,
    contentDescription = title,
    contentScale = ContentScale.Crop,
    modifier = Modifier.fillMaxSize(),
  )
}

@Composable
private fun MovieInfo(
  title: String,
  voteAverage: Float,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    MovieTitle(title)
    MovieRating(voteAverage)
  }
}

@Composable
private fun MovieTitle(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.bodyMedium,
    color = Color.White,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
  )
}

@Composable
private fun MovieRating(voteAverage: Float) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    RatingIcon()
    RatingText(voteAverage)
  }
}

@Composable
private fun RatingIcon() {
  Icon(
    imageVector = Icons.Default.Star,
    contentDescription = "Rating",
    tint = Color.Yellow,
    modifier = Modifier.size(16.dp),
  )
}

@Composable
private fun RatingText(voteAverage: Float) {
  Text(
    text = String.format("%.1f", voteAverage),
    style = MaterialTheme.typography.bodySmall,
    color = Color.White,
  )
}

@Composable
private fun FavoriteIcon(modifier: Modifier = Modifier) {
  Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = "Favorite",
    tint = MaterialTheme.colorScheme.primary,
    modifier = modifier.size(24.dp),
  )
}
