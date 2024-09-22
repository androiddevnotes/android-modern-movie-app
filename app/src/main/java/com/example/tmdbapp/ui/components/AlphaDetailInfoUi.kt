package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*

@Composable
internal fun AlphaDetailInfoUi(
  title: String,
  overview: String,
  releaseDate: String?,
  voteAverage: Float,
  textColor: Color,
) {
  Column(
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(16.dp),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.headlineLarge,
      color = textColor,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    releaseDate?.let { date ->
      Text(
        text = "Release Date: $date",
        style = MaterialTheme.typography.bodyMedium,
        color = textColor.copy(alpha = 0.7f),
      )
      Spacer(modifier = Modifier.height(8.dp))
    }
    Text(
      text = "Rating: $voteAverage",
      style = MaterialTheme.typography.bodyMedium,
      color = textColor.copy(alpha = 0.7f),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = "Overview",
      style = MaterialTheme.typography.titleMedium,
      color = textColor,
      fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = overview,
      style = MaterialTheme.typography.bodyMedium,
      color = textColor.copy(alpha = 0.9f),
    )
  }
}
