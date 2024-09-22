package com.example.tmdbapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import com.example.tmdbapp.R
import com.example.tmdbapp.models.AlphaCreateListUiState
import com.example.tmdbapp.models.AlphaCreateListUiState.*

@Composable
fun AlphaCreateListContentUi(
  listName: String,
  onListNameChange: (String) -> Unit,
  listDescription: String,
  onListDescriptionChange: (String) -> Unit,
  onCreateList: () -> Unit,
  alphaCreateListUiState: AlphaCreateListUiState<Int>,
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

  when (alphaCreateListUiState) {
    is Loading -> {
      CircularProgressIndicator()
    }

    is Success -> {
      Text(
        text = stringResource(R.string.list_created_success, alphaCreateListUiState.data),
        color = MaterialTheme.colorScheme.primary,
      )
    }

    is Error -> {
      Text(
        text = stringResource(R.string.list_creation_error, alphaCreateListUiState.message),
        color = MaterialTheme.colorScheme.error,
      )
    }

    Idle -> {
      // Do nothing or show a placeholder
    }
  }
}
