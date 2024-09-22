package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.AuthUiState
import com.example.tmdbapp.models.CreateListUiState
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun ItemViewModel.startAuthentication() {
  viewModelScope.launch {
    if (_authUiState.value == AuthUiState.Authenticated) return@launch

    _authUiState.value = AuthUiState.Loading
    when (val tokenResult = repository.createRequestToken()) {
      is Resource.Success -> {
        val token = tokenResult.data
        if (token != null) {
          _authUiState.value = AuthUiState.RequestTokenCreated(token)
        } else {
          _authUiState.value = AuthUiState.Error("Failed to create request token")
        }
      }

      is Resource.Error ->
        _authUiState.value =
          AuthUiState.Error(tokenResult.message ?: "Unknown error")
    }
  }
}

fun ItemViewModel.createSession(approvedToken: String) {
  viewModelScope.launch {
    _authUiState.value = AuthUiState.Loading
    when (val sessionResult = repository.createSession(approvedToken)) {
      is Resource.Success -> _authUiState.value = AuthUiState.Authenticated
      is Resource.Error ->
        _authUiState.value =
          AuthUiState.Error(sessionResult.message ?: "Failed to create session")
    }
  }
}

fun ItemViewModel.createList(
  name: String,
  description: String,
) {
  viewModelScope.launch {
    _createListUiState.value = CreateListUiState.Loading
    when (val result = repository.createList(name, description)) {
      is Resource.Success ->
        _createListUiState.value =
          result.data?.let { CreateListUiState.Success(it) }!!

      is Resource.Error ->
        _createListUiState.value =
          result.message?.let { CreateListUiState.Error(it) }!!
    }
  }
}

internal fun ItemViewModel.checkAuthenticationStatus() {
  viewModelScope.launch {
    sessionManager.sessionIdFlow.collect { sessionId ->
      _authUiState.value =
        if (sessionId != null) {
          AuthUiState.Authenticated
        } else {
          AuthUiState.Idle
        }
    }
  }
}
