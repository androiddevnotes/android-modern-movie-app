package com.example.tmdbapp.ui.screens

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
import com.example.tmdbapp.models.TmdbAuthUiState
import com.example.tmdbapp.ui.components.*
import com.example.tmdbapp.ui.viewmodel.*

@Composable
fun TmdbCreateListScreenUi(
  tmdbViewModel: TmdbViewModel,
  onNavigateBack: () -> Unit,
  application: Application,
) {
  var listName by remember { mutableStateOf("") }
  var listDescription by remember { mutableStateOf("") }
  val authState by tmdbViewModel.tmdbAuthUiState.collectAsState()
  val createListState by tmdbViewModel.tmdbCreateListUiState.collectAsState()

  LaunchedEffect(Unit) {
    tmdbViewModel.startAuthentication()
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
        is TmdbAuthUiState.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        is TmdbAuthUiState.Error -> {
          ErrorTextUi(error = (authState as TmdbAuthUiState.Error).error)
        }

        is TmdbAuthUiState.RequestTokenCreated -> {
          val token = (authState as TmdbAuthUiState.RequestTokenCreated).data
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
          Button(onClick = { tmdbViewModel.createSession(token) }) {
            Text(stringResource(R.string.approved_request))
          }
        }

        is TmdbAuthUiState.Authenticated -> {
          TmdbCreateListContentUi(
            listName = listName,
            onListNameChange = { listName = it },
            listDescription = listDescription,
            onListDescriptionChange = { listDescription = it },
            onCreateList = { tmdbViewModel.createList(listName, listDescription) },
            tmdbCreateListUiState = createListState,
          )
        }

        TmdbAuthUiState.Idle -> {
          // Do nothing or show a placeholder
        }
      }
    }
  }
}
