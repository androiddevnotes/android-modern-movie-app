package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.utils.Constants
import com.example.tmdbapp.utils.MovieError
import com.example.tmdbapp.viewmodel.MovieUiState
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun MovieListErrorView(
  errorState: MovieUiState.Error,
  viewModel: MovieViewModel,
  onSettingsClick: () -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text =
          when (val error = errorState.error) {
            is MovieError.ApiError -> stringResource(error.messageResId, error.errorMessage)
            else -> stringResource(error.messageResId)
          },
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp),
      )
      Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
      Button(onClick = { viewModel.loadMoreMovies() }) {
        Text("Retry")
      }

      if (errorState.error is MovieError.ApiKeyMissing) {
        Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))
        Button(onClick = onSettingsClick) {
          Text("Go to Settings")
        }
      }
    }
  }
}
