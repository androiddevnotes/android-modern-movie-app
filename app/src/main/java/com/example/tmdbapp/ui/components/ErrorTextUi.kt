package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.tmdbapp.utils.AppError
import com.example.tmdbapp.utils.Constants

@Composable
fun ErrorTextUi(
  error: AppError,
  modifier: Modifier = Modifier,
) {
  val errorMessage =
    when (error) {
      is AppError.ApiError -> stringResource(error.messageResId, error.errorMessage)
      else -> stringResource(error.messageResId)
    }

  Text(
    text = errorMessage,
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.error,
    textAlign = TextAlign.Center,
    modifier = modifier.padding(Constants.PADDING_MEDIUM),
  )
}
