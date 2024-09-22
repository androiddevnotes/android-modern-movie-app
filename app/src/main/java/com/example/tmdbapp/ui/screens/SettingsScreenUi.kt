package com.example.tmdbapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import com.example.tmdbapp.BuildConfig
import com.example.tmdbapp.R
import com.example.tmdbapp.ui.components.CommonTopBar
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsScreenUi(
  apiKeyManager: ApiKeyManager,
  onBackPress: () -> Unit,
) {
  var tmdbApiKey by remember { mutableStateOf("") }
  var openAiApiKey by remember { mutableStateOf("") }
  val scope = rememberCoroutineScope()

  LaunchedEffect(apiKeyManager) {
    tmdbApiKey = apiKeyManager.tmdbApiKeyFlow.first()
    openAiApiKey = apiKeyManager.openAiApiKeyFlow.first()
  }

  Scaffold(
    topBar = {
      CommonTopBar(
        title = stringResource(R.string.settings),
        onBackPress = onBackPress,
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
        onValueChange = { newValue ->
          tmdbApiKey = newValue
          scope.launch {
            apiKeyManager.setTmdbApiKey(newValue)
          }
        },
        label = { Text(stringResource(R.string.tmdb_api_key)) },
        placeholder = { Text(stringResource(R.string.enter_your_api_key)) },
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))

      OutlinedTextField(
        value = if (openAiApiKey == BuildConfig.OPENAI_API_KEY) "" else openAiApiKey,
        onValueChange = { newValue ->
          openAiApiKey = newValue
          scope.launch {
            apiKeyManager.setOpenAiApiKey(newValue)
          }
        },
        label = { Text(stringResource(R.string.openai_api_key)) },
        placeholder = { Text(stringResource(R.string.enter_your_api_key)) },
        modifier = Modifier.fillMaxWidth(),
      )

      Spacer(modifier = Modifier.height(Constants.PADDING_MEDIUM))

      Button(
        onClick = {
          scope.launch {
            apiKeyManager.setTmdbApiKey(tmdbApiKey)
            apiKeyManager.setOpenAiApiKey(openAiApiKey)
          }
        },
        modifier = Modifier.align(Alignment.CenterHorizontally),
      ) {
        Text("Save API Keys")
      }
    }
  }
}
