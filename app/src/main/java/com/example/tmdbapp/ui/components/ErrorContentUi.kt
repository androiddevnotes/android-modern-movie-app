package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import com.example.tmdbapp.R
import com.example.tmdbapp.utils.AppError
import com.example.tmdbapp.utils.Constants

@Composable
fun ErrorContentUi(
  error: AppError,
  onRetry: () -> Unit,
  onBackPress: (() -> Unit)? = null,
  onSettingsClick: (() -> Unit)? = null,
) {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(Constants.PADDING_MEDIUM),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    ErrorTextUi(error)
    Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
    Button(onClick = onRetry) {
      Text(stringResource(R.string.retry))
    }
    if (error is AppError.ApiKeyMissing && onSettingsClick != null) {
      Spacer(modifier = Modifier.height(Constants.PADDING_SMALL))
      Button(onClick = onSettingsClick) {
        Text(stringResource(R.string.settings))
      }
    }
    if (onBackPress != null) {
      Spacer(modifier = Modifier.height(Constants.PADDING_SMALL))
      TextButton(onClick = onBackPress) {
        Text(stringResource(R.string.back))
      }
    }
  }
}
