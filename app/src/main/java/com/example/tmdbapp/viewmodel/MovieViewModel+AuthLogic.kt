package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.AuthUiState
import com.example.tmdbapp.models.CreateListUiState
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun MovieViewModel.startAuthentication() {
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

fun MovieViewModel.createSession(approvedToken: String) {
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

fun MovieViewModel.createList(
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

internal fun MovieViewModel.checkAuthenticationStatus() {
  viewModelScope.launch {
    val sessionId = sessionManager.getSessionId()
    if (sessionId != null) {
      _authUiState.value = AuthUiState.Authenticated
    }
  }
}
