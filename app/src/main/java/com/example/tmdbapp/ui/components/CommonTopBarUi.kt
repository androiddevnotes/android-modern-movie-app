package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.tmdbapp.R

@Composable
fun CommonTopBar(
  title: String,
  onBackPress: (() -> Unit)? = null,
  actions: @Composable (RowScope.() -> Unit) = {},
) {
  TopAppBar(
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
      )
    },
    navigationIcon = {
      if (onBackPress != null) {
        IconButton(onClick = onBackPress) {
          Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
          )
        }
      }
    },
    actions = actions,
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
      ),
  )
}
