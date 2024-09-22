package com.example.tmdbapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.AlphaAuthUiState
import com.example.tmdbapp.models.AlphaCreateListUiState
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.repository.createList
import com.example.tmdbapp.repository.createSession
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun AlphaViewModel.startAuthentication() {
  viewModelScope.launch {
    if (_alphaAuthUiState.value == AlphaAuthUiState.Authenticated) return@launch

    _alphaAuthUiState.value = AlphaAuthUiState.Loading
    when (val tokenResult = repository.createRequestToken()) {
      is Resource.Success -> {
        val token = tokenResult.data
        if (token != null) {
          _alphaAuthUiState.value = AlphaAuthUiState.RequestTokenCreated(token)
        } else {
          _alphaAuthUiState.value = AlphaAuthUiState.Error(AppError.Unknown)
        }
      }
      is Resource.Error -> {
        _alphaAuthUiState.value = AlphaAuthUiState.Error(AppError.ApiError(tokenResult.message ?: "Unknown error"))
      }
    }
  }
}

fun AlphaViewModel.createSession(approvedToken: String) {
  viewModelScope.launch {
    _alphaAuthUiState.value = AlphaAuthUiState.Loading
    when (val sessionResult = repository.createSession(approvedToken)) {
      is Resource.Success -> _alphaAuthUiState.value = AlphaAuthUiState.Authenticated
      is Resource.Error ->
        _alphaAuthUiState.value =
          AlphaAuthUiState.Error(AppError.ApiError(sessionResult.message ?: "Failed to create session"))
    }
  }
}

fun AlphaViewModel.createList(
  name: String,
  description: String,
) {
  viewModelScope.launch {
    _alphaCreateListUiState.value = AlphaCreateListUiState.Loading
    when (val result = repository.createList(name, description)) {
      is Resource.Success -> {
        val listId = result.data
        if (listId != null) {
          _alphaCreateListUiState.value = AlphaCreateListUiState.Success(listId)
        } else {
          _alphaCreateListUiState.value = AlphaCreateListUiState.Error(AppError.Unknown)
        }
      }
      is Resource.Error ->
        _alphaCreateListUiState.value =
          AlphaCreateListUiState.Error(AppError.ApiError(result.message ?: "Unknown error"))
    }
  }
}

internal fun AlphaViewModel.checkAuthenticationStatus() {
  viewModelScope.launch {
    sessionManagerPreferencesDataStore.sessionIdFlow.collect { sessionId ->
      _alphaAuthUiState.value =
        if (sessionId != null) {
          AlphaAuthUiState.Authenticated
        } else {
          AlphaAuthUiState.Idle
        }
    }
  }
}
