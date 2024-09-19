package com.example.tmdbapp.viewmodel

sealed class AuthState {
  data object Idle : AuthState()

  data object Loading : AuthState()

  data class RequestTokenCreated(
    val token: String,
  ) : AuthState()

  data object Authenticated : AuthState()

  data class Error(
    val message: String,
  ) : AuthState()
}

sealed class CreateListState {
  data object Idle : CreateListState()

  data object Loading : CreateListState()

  data class Success(
    val listId: Int,
  ) : CreateListState()

  data class Error(
    val message: String,
  ) : CreateListState()
}
