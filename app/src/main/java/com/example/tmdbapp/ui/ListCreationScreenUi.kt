package com.example.tmdbapp.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.example.tmdbapp.R
import com.example.tmdbapp.models.AuthUiState
import com.example.tmdbapp.ui.components.CommonTopBar
import com.example.tmdbapp.viewmodel.*

@Composable
fun ListCreationScreen(
  movieViewModel: MovieViewModel,
  onNavigateBack: () -> Unit,
  application: Application,
) {
  var listName by remember { mutableStateOf("") }
  var listDescription by remember { mutableStateOf("") }
  val authState by movieViewModel.authUiState.collectAsState()
  val createListState by movieViewModel.createListUiState.collectAsState()

  LaunchedEffect(Unit) {
    movieViewModel.startAuthentication()
  }

  Scaffold(
    topBar = {
      CommonTopBar(
        title = stringResource(R.string.create_list),
        onBackPress = onNavigateBack,
      )
    },
  ) { paddingValues ->
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      when (authState) {
        is AuthUiState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        is AuthUiState.Error -> {
          Text(
            text = stringResource(R.string.auth_error, (authState as AuthUiState.Error).message),
            color = MaterialTheme.colorScheme.error,
          )
        }

        is AuthUiState.RequestTokenCreated -> {
          val token = (authState as AuthUiState.RequestTokenCreated<String>).data
          LaunchedEffect(token) {
            val intent =
              Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.themoviedb.org/authenticate/$token"),
              )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
          }
          Text(stringResource(R.string.approve_request))
          Button(onClick = { movieViewModel.createSession(token) }) {
            Text(stringResource(R.string.approved_request))
          }
        }

        is AuthUiState.Authenticated -> {
          ListCreationContentUi(
            listName = listName,
            onListNameChange = { listName = it },
            listDescription = listDescription,
            onListDescriptionChange = { listDescription = it },
            onCreateList = { movieViewModel.createList(listName, listDescription) },
            createListUiState = createListState,
          )
        }

        AuthUiState.Idle -> {
          // Do nothing or show a placeholder
        }
      }
    }
  }
}
