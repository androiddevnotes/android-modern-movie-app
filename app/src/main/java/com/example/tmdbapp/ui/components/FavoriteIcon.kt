package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.*

@Composable
fun FavoriteIcon(modifier: Modifier = Modifier) {
  Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = "Favorite",
    tint = MaterialTheme.colorScheme.primary,
    modifier = modifier.size(24.dp),
  )
}
