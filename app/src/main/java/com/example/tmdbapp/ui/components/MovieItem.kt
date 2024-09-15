package com.example.tmdbapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tmdbapp.models.Movie

@Composable
fun MovieItem(movie: Movie, modifier: Modifier = Modifier) {
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
                Text(text = movie.title, style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = movie.overview, style = MaterialTheme.typography.body2, maxLines = 4)
            }
        }
    }
}