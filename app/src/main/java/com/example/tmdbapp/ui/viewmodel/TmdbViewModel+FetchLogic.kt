package com.example.tmdbapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.ui.viewmodel.handlers.TmdbResultHandler
import kotlinx.coroutines.*

fun TmdbViewModel.fetchMovies() {
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
    TmdbResultHandler.handleTmdbResult(
      result,
      currentPage,
      _tmdbListUiState,
      apiKeyManager,
      { currentPage = it },
      { isLastPage = it },
      { isLoading = it },
    )
  }
}

fun TmdbViewModel.fetchMovieDetails(movieId: Int) {
  viewModelScope.launch {
    _tmdbDetailUiState.value = TmdbDetailUiState.Loading
    val result = repository.getMovieDetails(movieId)
    TmdbResultHandler.handleTmdbDetailResult(
      result,
      _tmdbDetailUiState,
      apiKeyManager,
      movieId,
    )
  }
}

internal fun TmdbViewModel.searchMovies(query: String) {
  viewModelScope.launch {
    _tmdbListUiState.value = TmdbListUiState.Loading
    val result = repository.searchMovies(query, 1)
    TmdbResultHandler.handleTmdbResult(
      result,
      currentPage,
      _tmdbListUiState,
      apiKeyManager,
      { currentPage = it },
      { isLastPage = it },
      { isLoading = it },
    )
  }
}
