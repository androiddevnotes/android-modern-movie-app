package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import com.example.tmdbapp.utils.*
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
    handleMovieResult(result)
  }
}

fun AlphaViewModel.fetchPopularMovies() {
  if (isLoading || isLastPage) return
  isLoading = true
  viewModelScope.launch {
    val result = repository.getPopularMovies(currentPage)
    handleMovieResult(result)
  }
}

internal fun AlphaViewModel.searchMovies(query: String) {
  viewModelScope.launch {
    _alphaListUiState.value = ItemListUiState.Loading
    val result = repository.searchMovies(query, 1)
    handleMovieResult(result)
  }
}

private fun AlphaViewModel.handleMovieResult(result: Resource<MovieResponse>) {
  when (result) {
    is Resource.Success -> {
      val newMovies = result.data?.results ?: emptyList()
      val currentMovies =
        if (_alphaListUiState.value is ItemListUiState.Success && currentPage > 1) {
          (_alphaListUiState.value as ItemListUiState.Success<List<Movie>>).data
        } else {
          emptyList()
        }
      _alphaListUiState.value = ItemListUiState.Success(currentMovies + newMovies)
      currentPage++
      isLastPage = newMovies.isEmpty()
    }

    is Resource.Error -> {
      _alphaListUiState.value = ItemListUiState.Error(handleNetworkError(result.message, apiKeyManager))
    }
  }
  isLoading = false
}
