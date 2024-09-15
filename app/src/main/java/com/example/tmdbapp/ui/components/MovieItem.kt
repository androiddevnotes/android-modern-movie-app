package com.example.tmdbapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tmdbapp.models.Movie

@Composable
fun MovieItem(
    movie: Movie, 
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                    error = rememberAsyncImagePainter(model = "https://via.placeholder.com/150")
                ),
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = movie.title, style = MaterialTheme.typography.h6)
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (movie.isFavorite) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = movie.overview, style = MaterialTheme.typography.body2, maxLines = 4)
            }
        }
    }
}