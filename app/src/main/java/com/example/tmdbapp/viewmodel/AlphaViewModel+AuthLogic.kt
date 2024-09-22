package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.ItemAuthUiState
import com.example.tmdbapp.models.ItemCreateListUiState
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun AlphaViewModel.startAuthentication() {
  viewModelScope.launch {
    if (_alphaAuthUiState.value == ItemAuthUiState.Authenticated) return@launch

    _alphaAuthUiState.value = ItemAuthUiState.Loading
    when (val tokenResult = repository.createRequestToken()) {
      is Resource.Success -> {
        val token = tokenResult.data
        if (token != null) {
          _alphaAuthUiState.value = ItemAuthUiState.RequestTokenCreated(token)
        } else {
          _alphaAuthUiState.value = ItemAuthUiState.Error("Failed to create request token")
        }
      }

      is Resource.Error ->
        _alphaAuthUiState.value =
          ItemAuthUiState.Error(tokenResult.message ?: "Unknown error")
    }
  }
}

fun AlphaViewModel.createSession(approvedToken: String) {
  viewModelScope.launch {
    _alphaAuthUiState.value = ItemAuthUiState.Loading
    when (val sessionResult = repository.createSession(approvedToken)) {
      is Resource.Success -> _alphaAuthUiState.value = ItemAuthUiState.Authenticated
      is Resource.Error ->
        _alphaAuthUiState.value =
          ItemAuthUiState.Error(sessionResult.message ?: "Failed to create session")
    }
  }
}

fun AlphaViewModel.createList(
  name: String,
  description: String,
) {
  viewModelScope.launch {
    _alphaCreateListUiState.value = ItemCreateListUiState.Loading
    when (val result = repository.createList(name, description)) {
      is Resource.Success ->
        _alphaCreateListUiState.value =
          result.data?.let { ItemCreateListUiState.Success(it) }!!

      is Resource.Error ->
        _alphaCreateListUiState.value =
          result.message?.let { ItemCreateListUiState.Error(it) }!!
    }
  }
}

internal fun AlphaViewModel.checkAuthenticationStatus() {
  viewModelScope.launch {
    sessionManagerPreferencesDataStore.sessionIdFlow.collect { sessionId ->
      _alphaAuthUiState.value =
        if (sessionId != null) {
          ItemAuthUiState.Authenticated
        } else {
          ItemAuthUiState.Idle
        }
    }
  }
}
