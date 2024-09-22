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
import com.example.tmdbapp.models.CreateListUiState
import com.example.tmdbapp.ui.components.CommonTopBar
import com.example.tmdbapp.viewmodel.*

@Composable
fun ListCreationScreen(
  viewModel: MovieViewModel,
  onNavigateBack: () -> Unit,
  application: Application,
) {
  var listName by remember { mutableStateOf("") }
  var listDescription by remember { mutableStateOf("") }
  val authState by viewModel.authUiState.collectAsState()
  val createListState by viewModel.createListUiState.collectAsState()

  LaunchedEffect(Unit) {
    viewModel.startAuthentication()
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
          Button(onClick = { viewModel.createSession(token) }) {
            Text(stringResource(R.string.approved_request))
          }
        }

        is AuthUiState.Authenticated -> {
          ListCreationContent(
            listName = listName,
            onListNameChange = { listName = it },
            listDescription = listDescription,
            onListDescriptionChange = { listDescription = it },
            onCreateList = { viewModel.createList(listName, listDescription) },
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

@Composable
private fun ListCreationContent(
  listName: String,
  onListNameChange: (String) -> Unit,
  listDescription: String,
  onListDescriptionChange: (String) -> Unit,
  onCreateList: () -> Unit,
  createListUiState: CreateListUiState<Int>,
) {
  OutlinedTextField(
    value = listName,
    onValueChange = onListNameChange,
    label = { Text(stringResource(R.string.list_name)) },
    modifier = Modifier.fillMaxWidth(),
  )

  OutlinedTextField(
    value = listDescription,
    onValueChange = onListDescriptionChange,
    label = { Text(stringResource(R.string.list_description)) },
    modifier = Modifier.fillMaxWidth(),
  )

  Button(
    onClick = onCreateList,
  ) {
    Text(stringResource(R.string.create_list))
  }

  when (createListUiState) {
    is CreateListUiState.Loading -> {
      CircularProgressIndicator()
    }

    is CreateListUiState.Success -> {
      Text(
        text = stringResource(R.string.list_created_success, createListUiState.data),
        color = MaterialTheme.colorScheme.primary,
      )
    }

    is CreateListUiState.Error -> {
      Text(
        text = stringResource(R.string.list_creation_error, createListUiState.message),
        color = MaterialTheme.colorScheme.error,
      )
    }

    CreateListUiState.Idle -> {
      // Do nothing or show a placeholder
    }
  }
}
