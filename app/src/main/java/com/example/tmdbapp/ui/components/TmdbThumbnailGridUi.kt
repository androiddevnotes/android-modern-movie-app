package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.layout.*
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun GridItemThumbnailUi(
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
