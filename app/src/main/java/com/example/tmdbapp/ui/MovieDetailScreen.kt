package com.example.tmdbapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun MovieDetailScreen(
    viewModel: MovieViewModel,
    onBackPress: () -> Unit
) {
    val currentMovie by viewModel.currentMovie.collectAsState()

    currentMovie?.let { movie ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = movie.title) },
                    navigationIcon = {
                        IconButton(onClick = onBackPress) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleFavorite(movie) }) {
                            Icon(
                                imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (movie.isFavorite) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
                    ),
                    contentDescription = movie.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = movie.title, style = MaterialTheme.typography.h5)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = movie.overview, style = MaterialTheme.typography.body1)
                }
            }
        }
    } ?: run {
        // Handle case when no movie is selected
        Text("No movie selected")
    }
}