package com.example.tmdbapp.viewmodel

import androidx.lifecycle.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import com.example.tmdbapp.utils.*
import kotlinx.coroutines.*

fun MovieViewModel.fetchMovies() {
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

fun MovieViewModel.fetchPopularMovies() {
  if (isLoading || isLastPage) return
  isLoading = true
  viewModelScope.launch {
    val result = repository.getPopularMovies(currentPage)
    handleMovieResult(result)
  }
}

internal fun MovieViewModel.searchMovies(query: String) {
  viewModelScope.launch {
    _List_uiState.value = ListUiState.Loading
    val result = repository.searchMovies(query, 1)
    handleMovieResult(result)
  }
}

private fun MovieViewModel.handleMovieResult(result: Resource<MovieResponse>) {
  when (result) {
    is Resource.Success -> {
      val newMovies = result.data?.results ?: emptyList()
      val currentMovies =
        if (_List_uiState.value is ListUiState.Success && currentPage > 1) {
          (_List_uiState.value as ListUiState.Success<List<Movie>>).data
        } else {
          emptyList()
        }
      _List_uiState.value = ListUiState.Success(currentMovies + newMovies)
      currentPage++
      isLastPage = newMovies.isEmpty()
    }

    is Resource.Error -> {
      _List_uiState.value = ListUiState.Error(handleError(result.message, apiKeyManager))
    }
  }
  isLoading = false
}
