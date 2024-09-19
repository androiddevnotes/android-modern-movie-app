package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun MovieViewModel.startAuthentication() {
  viewModelScope.launch {
    if (_authState.value == AuthState.Authenticated) return@launch

    _authState.value = AuthState.Loading
    when (val tokenResult = repository.createRequestToken()) {
      is Resource.Success -> {
        val token = tokenResult.data
        if (token != null) {
          _authState.value = AuthState.RequestTokenCreated(token)
        } else {
          _authState.value = AuthState.Error("Failed to create request token")
        }
      }

      is Resource.Error ->
        _authState.value =
          AuthState.Error(tokenResult.message ?: "Unknown error")
    }
  }
}

fun MovieViewModel.createSession(approvedToken: String) {
  viewModelScope.launch {
    _authState.value = AuthState.Loading
    when (val sessionResult = repository.createSession(approvedToken)) {
      is Resource.Success -> _authState.value = AuthState.Authenticated
      is Resource.Error ->
        _authState.value =
          AuthState.Error(sessionResult.message ?: "Failed to create session")
    }
  }
}

fun MovieViewModel.createList(
  name: String,
  description: String,
) {
  viewModelScope.launch {
    _createListState.value = CreateListState.Loading
    when (val result = repository.createList(name, description)) {
      is Resource.Success ->
        _createListState.value =
          result.data?.let { CreateListState.Success(it) }!!

      is Resource.Error ->
        _createListState.value =
          result.message?.let { CreateListState.Error(it) }!!
    }
  }
}

internal fun MovieViewModel.checkAuthenticationStatus() {
  viewModelScope.launch {
    val sessionId = sessionManager.getSessionId()
    if (sessionId != null) {
      _authState.value = AuthState.Authenticated
    }
  }
}
