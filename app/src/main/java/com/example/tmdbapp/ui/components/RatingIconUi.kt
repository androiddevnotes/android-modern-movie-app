package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*

@Composable
internal fun RatingIconUi() {
  Icon(
    imageVector = Icons.Default.Star,
    contentDescription = "Rating",
    tint = Color.Yellow,
    modifier = Modifier.size(16.dp),
  )
}
