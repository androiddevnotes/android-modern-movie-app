package com.example.tmdbapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.BuildConfig
import com.example.tmdbapp.R
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Constants

@Composable
fun SettingsScreen(
  apiKeyManager: ApiKeyManager,
  onBackPress: () -> Unit,
) {
  var tmdbApiKey by remember { mutableStateOf(apiKeyManager.getTmdbApiKey()) }
  var openAiApiKey by remember { mutableStateOf(apiKeyManager.getOpenAiApiKey()) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackPress) {
            Icon(
              Icons.Filled.ArrowBack,
              contentDescription = stringResource(R.string.back),
            )
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
          ),
      )
    },
  ) { paddingValues ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(Constants.PADDING_MEDIUM),
    ) {
      OutlinedTextField(
        value = if (tmdbApiKey == BuildConfig.TMDB_API_KEY) "" else tmdbApiKey,
        onValueChange = {
          tmdbApiKey = it
          apiKeyManager.setTmdbApiKey(it)
        },
        label = { Text(stringResource(R.string.tmdb_api_key)) },
        placeholder = { Text(stringResource(R.string.enter_your_api_key)) },
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))

      OutlinedTextField(
        value = if (openAiApiKey == BuildConfig.OPENAI_API_KEY) "" else openAiApiKey,
        onValueChange = {
          openAiApiKey = it
          apiKeyManager.setOpenAiApiKey(it)
        },
        label = { Text(stringResource(R.string.openai_api_key)) },
        placeholder = { Text(stringResource(R.string.enter_your_api_key)) },
        modifier = Modifier.fillMaxWidth(),
      )
    }
  }
}
