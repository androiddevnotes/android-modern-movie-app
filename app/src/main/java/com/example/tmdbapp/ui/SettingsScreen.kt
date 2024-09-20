package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.BuildConfig
import com.example.tmdbapp.R
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun SettingsScreen(viewModel: MovieViewModel) {
  var tmdbApiKey by remember { mutableStateOf(viewModel.getTmdbApiKey()) }
  var openAiApiKey by remember { mutableStateOf(viewModel.getOpenAiApiKey()) }

  Column(
    modifier =
      Modifier
        .fillMaxSize()
        .padding(16.dp),
  ) {
    Text(
      text = stringResource(R.string.settings),
      style = MaterialTheme.typography.headlineMedium,
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
      value = if (tmdbApiKey == BuildConfig.TMDB_API_KEY) "" else tmdbApiKey,
      onValueChange = {
        tmdbApiKey = it
        viewModel.setTmdbApiKey(it)
      },
      label = { Text(stringResource(R.string.tmdb_api_key)) },
      placeholder = { Text(stringResource(R.string.enter_your_api_key)) },
      modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
      value = if (openAiApiKey == BuildConfig.OPENAI_API_KEY) "" else openAiApiKey,
      onValueChange = {
        openAiApiKey = it
        viewModel.setOpenAiApiKey(it)
      },
      label = { Text(stringResource(R.string.openai_api_key)) },
      placeholder = { Text(stringResource(R.string.enter_your_api_key)) },
      modifier = Modifier.fillMaxWidth(),
    )
  }
}
