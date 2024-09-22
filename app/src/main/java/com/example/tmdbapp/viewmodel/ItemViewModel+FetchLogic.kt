package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun ItemViewModel.fetchMovies() {
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

fun ItemViewModel.fetchPopularMovies() {
  if (isLoading || isLastPage) return
  isLoading = true
  viewModelScope.launch {
    val result = repository.getPopularMovies(currentPage)
    handleMovieResult(result)
  }
}

internal fun ItemViewModel.searchMovies(query: String) {
  viewModelScope.launch {
    _Item_listUiState.value = ItemListUiState.Loading
    val result = repository.searchMovies(query, 1)
    handleMovieResult(result)
  }
}

private fun ItemViewModel.handleMovieResult(result: Resource<MovieResponse>) {
  when (result) {
    is Resource.Success -> {
      val newMovies = result.data?.results ?: emptyList()
      val currentMovies =
        if (_Item_listUiState.value is ItemListUiState.Success && currentPage > 1) {
          (_Item_listUiState.value as ItemListUiState.Success<List<Movie>>).data
        } else {
          emptyList()
        }
      _Item_listUiState.value = ItemListUiState.Success(currentMovies + newMovies)
      currentPage++
      isLastPage = newMovies.isEmpty()
    }

    is Resource.Error -> {
      _Item_listUiState.value = ItemListUiState.Error(handleNetworkError(result.message, apiKeyManager))
    }
  }
  isLoading = false
}
