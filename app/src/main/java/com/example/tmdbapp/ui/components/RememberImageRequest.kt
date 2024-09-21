package com.example.tmdbapp.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import coil.request.ImageRequest.Builder
import com.example.tmdbapp.utils.Constants

@Composable
fun rememberImageRequest(
  posterPath: String?,
  context: Context,
) = remember(posterPath) {
  Builder(context)
    .data(Constants.BASE_IMAGE_URL + posterPath)
    .crossfade(true)
    .build()
}
