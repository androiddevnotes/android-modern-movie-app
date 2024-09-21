package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.*

@Composable
fun MovieInfo(
  title: String,
  voteAverage: Float,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    MovieTitle(title)
    MovieRating(voteAverage)
  }
}
