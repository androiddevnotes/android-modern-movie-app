package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.R
import com.example.tmdbapp.utils.MovieError

@Composable
fun ErrorContent(
  error: MovieError,
  onRetry: () -> Unit,
  onBackPress: () -> Unit,
) {
  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = stringResource(error.messageResId),
      style = MaterialTheme.typography.headlineSmall,
      textAlign = TextAlign.Center,
    )
    Spacer(
      modifier =
        androidx.compose.ui.Modifier
          .height(16.dp),
    )
    Button(onClick = onRetry) {
      Text(stringResource(R.string.retry))
    }
    Spacer(
      modifier =
        androidx.compose.ui.Modifier
          .height(8.dp),
    )
    TextButton(onClick = onBackPress) {
      Text(stringResource(R.string.back))
    }
  }
}
