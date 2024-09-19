package com.example.tmdbapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.utils.Constants

@Composable
fun MovieItem(
  movie: Movie,
  modifier: Modifier = Modifier,
  onFavoriteClick: () -> Unit,
) {
  Card(
    modifier =
      modifier
        .fillMaxWidth()
        .height(150.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    shape = RoundedCornerShape(8.dp),
  ) {
    Row(
      modifier =
        Modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.surface),
    ) {
      AsyncImage(
        model = "${Constants.BASE_IMAGE_URL}${movie.posterPath}",
        contentDescription = movie.title,
        contentScale = ContentScale.Crop,
        modifier =
          Modifier
            .width(100.dp)
            .fillMaxHeight()
            .clip(
              RoundedCornerShape(
                topStart = 8.dp,
                bottomStart = 8.dp,
              ),
            ),
      )
      Column(
        modifier =
          Modifier
            .weight(1f)
            .padding(16.dp),
      ) {
        Text(
          text = movie.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = movie.overview,
          style = MaterialTheme.typography.bodySmall,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = String.format("%.1f", movie.voteAverage),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      IconButton(
        onClick = onFavoriteClick,
        modifier =
          Modifier
            .align(Alignment.Top),
      ) {
        Icon(
          imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
          contentDescription = "Favorite",
          tint = if (movie.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        )
      }
    }
  }
}
