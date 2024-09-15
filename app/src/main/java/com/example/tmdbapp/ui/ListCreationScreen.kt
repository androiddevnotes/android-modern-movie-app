package com.example.tmdbapp.ui

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
import com.example.tmdbapp.viewmodel.MovieViewModel
import com.example.tmdbapp.viewmodel.AuthState
import com.example.tmdbapp.viewmodel.CreateListState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCreationScreen(
    viewModel: MovieViewModel,
    onNavigateBack: () -> Unit
) {
    var listName by remember { mutableStateOf("") }
    var listDescription by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val createListState by viewModel.createListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.authenticate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_list)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is AuthState.Error -> {
                    Text(
                        text = stringResource(R.string.auth_error, (authState as AuthState.Error).message),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is AuthState.Authenticated -> {
                    OutlinedTextField(
                        value = listName,
                        onValueChange = { listName = it },
                        label = { Text(stringResource(R.string.list_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = listDescription,
                        onValueChange = { listDescription = it },
                        label = { Text(stringResource(R.string.list_description)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { viewModel.createList(listName, listDescription) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.create_list))
                    }

                    when (createListState) {
                        is CreateListState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        is CreateListState.Success -> {
                            Text(
                                text = stringResource(R.string.list_created_success, (createListState as CreateListState.Success).listId),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        is CreateListState.Error -> {
                            Text(
                                text = stringResource(R.string.list_creation_error, (createListState as CreateListState.Error).message),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> {}
                    }
                }
                else -> {}
            }
        }
    }
}