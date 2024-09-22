package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.ItemAuthUiState
import com.example.tmdbapp.models.ItemCreateListUiState
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun ItemViewModel.startAuthentication() {
  viewModelScope.launch {
    if (_Item_authUiState.value == ItemAuthUiState.Authenticated) return@launch

    _Item_authUiState.value = ItemAuthUiState.Loading
    when (val tokenResult = repository.createRequestToken()) {
      is Resource.Success -> {
        val token = tokenResult.data
        if (token != null) {
          _Item_authUiState.value = ItemAuthUiState.RequestTokenCreated(token)
        } else {
          _Item_authUiState.value = ItemAuthUiState.Error("Failed to create request token")
        }
      }

      is Resource.Error ->
        _Item_authUiState.value =
          ItemAuthUiState.Error(tokenResult.message ?: "Unknown error")
    }
  }
}

fun ItemViewModel.createSession(approvedToken: String) {
  viewModelScope.launch {
    _Item_authUiState.value = ItemAuthUiState.Loading
    when (val sessionResult = repository.createSession(approvedToken)) {
      is Resource.Success -> _Item_authUiState.value = ItemAuthUiState.Authenticated
      is Resource.Error ->
        _Item_authUiState.value =
          ItemAuthUiState.Error(sessionResult.message ?: "Failed to create session")
    }
  }
}

fun ItemViewModel.createList(
  name: String,
  description: String,
) {
  viewModelScope.launch {
    _Item_createListUiState.value = ItemCreateListUiState.Loading
    when (val result = repository.createList(name, description)) {
      is Resource.Success ->
        _Item_createListUiState.value =
          result.data?.let { ItemCreateListUiState.Success(it) }!!

      is Resource.Error ->
        _Item_createListUiState.value =
          result.message?.let { ItemCreateListUiState.Error(it) }!!
    }
  }
}

internal fun ItemViewModel.checkAuthenticationStatus() {
  viewModelScope.launch {
    sessionManagerPreferencesDataStore.sessionIdFlow.collect { sessionId ->
      _Item_authUiState.value =
        if (sessionId != null) {
          ItemAuthUiState.Authenticated
        } else {
          ItemAuthUiState.Idle
        }
    }
  }
}
