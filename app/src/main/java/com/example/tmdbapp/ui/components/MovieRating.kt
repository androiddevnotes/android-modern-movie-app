package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.Alignment.*
import androidx.compose.ui.unit.*

@Composable
fun MovieRating(voteAverage: Float) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    RatingIcon()
    Spacer(modifier = Modifier.width(4.dp))
    RatingText(voteAverage)
  }
}
