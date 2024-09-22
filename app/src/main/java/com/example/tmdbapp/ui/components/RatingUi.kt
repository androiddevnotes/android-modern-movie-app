package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun RatingUi(voteAverage: Float) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    RatingIconUi()
    Spacer(modifier = Modifier.width(4.dp))
    RatingTextUi(voteAverage)
  }
}
