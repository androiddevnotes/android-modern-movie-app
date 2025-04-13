package com.example.tmdbapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.TmdbAuthUiState
import com.example.tmdbapp.models.AlphaCreateListUiState
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.repository.createList
import com.example.tmdbapp.repository.createSession
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun TmdbViewModel.startAuthentication() {
  viewModelScope.launch {
    if (_tmdbAuthUiState.value == TmdbAuthUiState.Authenticated) return@launch

    _tmdbAuthUiState.value = TmdbAuthUiState.Loading
    when (val tokenResult = repository.createRequestToken()) {
      is Resource.Success -> {
        val token = tokenResult.data
        if (token != null) {
          _tmdbAuthUiState.value = TmdbAuthUiState.RequestTokenCreated(token)
        } else {
          _tmdbAuthUiState.value = TmdbAuthUiState.Error(AppError.Unknown)
        }
      }
      is Resource.Error -> {
        _tmdbAuthUiState.value = TmdbAuthUiState.Error(AppError.ApiError(tokenResult.message ?: "Unknown error"))
      }
    }
  }
}

fun TmdbViewModel.createSession(approvedToken: String) {
  viewModelScope.launch {
    _tmdbAuthUiState.value = TmdbAuthUiState.Loading
    when (val sessionResult = repository.createSession(approvedToken)) {
      is Resource.Success -> _tmdbAuthUiState.value = TmdbAuthUiState.Authenticated
      is Resource.Error ->
        _tmdbAuthUiState.value =
          TmdbAuthUiState.Error(AppError.ApiError(sessionResult.message ?: "Failed to create session"))
    }
  }
}

fun TmdbViewModel.createList(
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

internal fun TmdbViewModel.checkAuthenticationStatus() {
  viewModelScope.launch {
    sessionManagerPreferencesDataStore.sessionIdFlow.collect { sessionId ->
      _tmdbAuthUiState.value =
        if (sessionId != null) {
          TmdbAuthUiState.Authenticated
        } else {
          TmdbAuthUiState.Idle
        }
    }
  }
}
