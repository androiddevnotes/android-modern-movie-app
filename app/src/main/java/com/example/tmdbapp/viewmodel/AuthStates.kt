package com.example.tmdbapp.viewmodel

sealed class AuthState {
  object Idle : AuthState()

  object Loading : AuthState()

  data class RequestTokenCreated(
    val token: String,
  ) : AuthState()

  object Authenticated : AuthState()

  data class Error(
    val message: String,
  ) : AuthState()
}

sealed class CreateListState {
  object Idle : CreateListState()

  object Loading : CreateListState()

  data class Success(
    val listId: Int,
  ) : CreateListState()

  data class Error(
    val message: String,
  ) : CreateListState()
}
