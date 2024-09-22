package com.example.tmdbapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.ui.viewmodel.handlers.AlphaResultHandler
import kotlinx.coroutines.*

fun AlphaViewModel.fetchMovies() {
  if (isLoading || isLastPage) return
  isLoading = true
  viewModelScope.launch {
    val result =
      repository.discoverMovies(
        page = currentPage,
        sortBy = _currentSortOptions.value.apiValue,
        genres = _filterOptions.value.genres,
        releaseYear = _filterOptions.value.releaseYear,
        minRating = _filterOptions.value.minRating,
      )
    AlphaResultHandler.handleAlphaResult(
      result,
      currentPage,
      _alphaListUiState,
      apiKeyManager,
      { currentPage = it },
      { isLastPage = it },
      { isLoading = it },
    )
  }
}

internal fun AlphaViewModel.searchMovies(query: String) {
  viewModelScope.launch {
    _alphaListUiState.value = AlphaListUiState.Loading
    val result = repository.searchMovies(query, 1)
    AlphaResultHandler.handleAlphaResult(
      result,
      currentPage,
      _alphaListUiState,
      apiKeyManager,
      { currentPage = it },
      { isLastPage = it },
      { isLoading = it },
    )
  }
}
