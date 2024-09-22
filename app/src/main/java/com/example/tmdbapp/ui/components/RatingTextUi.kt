package com.example.tmdbapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.*

@Composable
internal fun RatingTextUi(voteAverage: Float) {
  Text(
    text = String.format("%.1f", voteAverage),
    style = MaterialTheme.typography.bodySmall,
    color = Color.White,
  )
}
