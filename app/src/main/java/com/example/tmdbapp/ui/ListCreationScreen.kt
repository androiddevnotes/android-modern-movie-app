package com.example.tmdbapp.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tmdbapp.R
import com.example.tmdbapp.viewmodel.AuthState
import com.example.tmdbapp.viewmodel.CreateListState
import com.example.tmdbapp.viewmodel.MovieViewModel

@Composable
fun ListCreationScreen(
  viewModel: MovieViewModel,
  onNavigateBack: () -> Unit,
  application: Application,
) {
  var listName by remember { mutableStateOf("") }
  var listDescription by remember { mutableStateOf("") }
  val authState by viewModel.authState.collectAsState()
  val createListState by viewModel.createListState.collectAsState()

  LaunchedEffect(Unit) {
    viewModel.startAuthentication()
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.create_list)) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
          }
        },
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
        is AuthState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        is AuthState.Error -> {
          Text(
            text = stringResource(R.string.auth_error, (authState as AuthState.Error).message),
            color = MaterialTheme.colorScheme.error,
          )
        }
        is AuthState.RequestTokenCreated -> {
          val token = (authState as AuthState.RequestTokenCreated).token
          LaunchedEffect(token) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/authenticate/$token"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
          }
          Text(stringResource(R.string.approve_request))
          Button(onClick = { viewModel.createSession(token) }) {
            Text(stringResource(R.string.approved_request))
          }
        }
        is AuthState.Authenticated -> {
          ListCreationContent(
            listName = listName,
            onListNameChange = { listName = it },
            listDescription = listDescription,
            onListDescriptionChange = { listDescription = it },
            onCreateList = { viewModel.createList(listName, listDescription) },
            createListState = createListState,
          )
        }
        else -> {}
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
  createListState: CreateListState,
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

  when (createListState) {
    is CreateListState.Loading -> {
      CircularProgressIndicator()
    }
    is CreateListState.Success -> {
      Text(
        text = stringResource(R.string.list_created_success, createListState.listId),
        color = MaterialTheme.colorScheme.primary,
      )
    }
    is CreateListState.Error -> {
      Text(
        text = stringResource(R.string.list_creation_error, createListState.message),
        color = MaterialTheme.colorScheme.error,
      )
    }
    else -> {}
  }
}
