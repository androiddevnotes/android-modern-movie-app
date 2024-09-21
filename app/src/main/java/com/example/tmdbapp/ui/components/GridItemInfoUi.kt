package com.example.tmdbapp.ui.components

import TruncatedTitle
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.*

@Composable
fun GridItemInfoUi(
  title: String,
  voteAverage: Float,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    TruncatedTitle(title)
    RatingUi(voteAverage)
  }
}
