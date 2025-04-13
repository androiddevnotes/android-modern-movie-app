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
    TmdbResultHandler.handleAlphaResult(
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

fun TmdbViewModel.fetchMovieDetails(movieId: Int) {
  viewModelScope.launch {
    _tmdbDetailUiState.value = TmdbDetailUiState.Loading
    val result = repository.getMovieDetails(movieId)
    TmdbResultHandler.handleAlphaDetailResult(
      result,
      _tmdbDetailUiState,
      apiKeyManager,
      movieId,
    )
  }
}

internal fun TmdbViewModel.searchMovies(query: String) {
  viewModelScope.launch {
    _alphaListUiState.value = AlphaListUiState.Loading
    val result = repository.searchMovies(query, 1)
    TmdbResultHandler.handleAlphaResult(
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
