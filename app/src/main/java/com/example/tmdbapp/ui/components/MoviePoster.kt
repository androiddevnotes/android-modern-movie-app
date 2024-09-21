package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.layout.ContentScale.*
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun MoviePoster(
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
