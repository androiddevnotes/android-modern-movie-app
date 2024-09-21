package com.example.tmdbapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.*

@Composable
internal fun RatingText(voteAverage: Float) {
  Text(
    text = String.format("%.1f", voteAverage),
    style = MaterialTheme.typography.bodySmall,
    color = Color.White,
  )
}
