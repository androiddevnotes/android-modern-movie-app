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
import com.example.tmdbapp.models.AlphaAuthUiState
import com.example.tmdbapp.ui.components.CommonTopBar
import com.example.tmdbapp.viewmodel.*

@Composable
fun AlphaCreateListScreenUi(
  alphaViewModel: AlphaViewModel,
  onNavigateBack: () -> Unit,
  application: Application,
) {
  var listName by remember { mutableStateOf("") }
  var listDescription by remember { mutableStateOf("") }
  val authState by alphaViewModel.alphaAuthUiState.collectAsState()
  val createListState by alphaViewModel.alphaCreateListUiState.collectAsState()

  LaunchedEffect(Unit) {
    alphaViewModel.startAuthentication()
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
        is AlphaAuthUiState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        is AlphaAuthUiState.Error -> {
          Text(
            text = stringResource(R.string.auth_error, (authState as AlphaAuthUiState.Error).message),
            color = MaterialTheme.colorScheme.error,
          )
        }

        is AlphaAuthUiState.RequestTokenCreated -> {
          val token = (authState as AlphaAuthUiState.RequestTokenCreated<String>).data
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
          Button(onClick = { alphaViewModel.createSession(token) }) {
            Text(stringResource(R.string.approved_request))
          }
        }

        is AlphaAuthUiState.Authenticated -> {
          AlphaCreateListContentUi(
            listName = listName,
            onListNameChange = { listName = it },
            listDescription = listDescription,
            onListDescriptionChange = { listDescription = it },
            onCreateList = { alphaViewModel.createList(listName, listDescription) },
            alphaCreateListUiState = createListState,
          )
        }

        AlphaAuthUiState.Idle -> {
          // Do nothing or show a placeholder
        }
      }
    }
  }
}
