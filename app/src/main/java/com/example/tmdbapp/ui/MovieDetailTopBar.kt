package com.example.tmdbapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import com.example.tmdbapp.R
import com.example.tmdbapp.utils.Constants

@Composable
fun MovieDetailTopBar(
  onBackPress: () -> Unit,
  onFavoriteClick: () -> Unit,
  onDownloadClick: () -> Unit,
  onAskAIClick: () -> Unit,
  isFavorite: Boolean,
) {
  TopAppBar(
    title = { },
    navigationIcon = {
      IconButton(onClick = onBackPress) {
        Icon(
          Filled.ArrowBack,
          contentDescription = stringResource(R.string.back),
          tint = Color.White,
        )
      }
    },
    actions = {
      IconButton(onClick = onFavoriteClick) {
        Icon(
          imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
          contentDescription = stringResource(R.string.favorite),
          tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White,
        )
      }
      IconButton(onClick = onDownloadClick) {
        Image(
          painter = painterResource(id = R.drawable.my_shape_poly12),
          contentDescription = stringResource(R.string.download_image),
          modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
        )
      }
      IconButton(onClick = onAskAIClick) {
        Image(
          painter = painterResource(id = R.drawable.cool_shape_ai),
          contentDescription = stringResource(R.string.ask_ai_about_movie),
          modifier = Modifier.size(Constants.ICON_SIZE_SMALL),
        )
      }
    },
    colors =
      TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        titleContentColor = Color.White,
        navigationIconContentColor = Color.White,
        actionIconContentColor = Color.White,
      ),
  )
}
