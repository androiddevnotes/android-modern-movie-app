package com.example.tmdbapp.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.launch

fun MovieViewModel.fetchMovies() {
  if (isLoading || isLastPage) return
  isLoading = true
  viewModelScope.launch {
    try {
      val result =
        repository.discoverMovies(
          page = currentPage,
          sortBy = _currentSortOption.value.apiValue,
          genres = _filterOptions.value.genres,
          releaseYear = _filterOptions.value.releaseYear,
          minRating = _filterOptions.value.minRating,
        )
      when (result) {
        is Resource.Success -> {
          val newMovies = result.data?.results ?: emptyList()
          val currentMovies =
            if (_uiState.value is MovieUiState.Success && currentPage > 1) {
              (_uiState.value as MovieUiState.Success).movies
            } else {
              emptyList()
            }
          _uiState.value = MovieUiState.Success(currentMovies + newMovies)
          currentPage++
          isLastPage = newMovies.isEmpty()
        }
        is Resource.Error -> {
          _uiState.value = MovieUiState.Error(handleError(result.message))
        }
      }
    } catch (e: Exception) {
      _uiState.value = MovieUiState.Error(handleError(e))
    } finally {
      isLoading = false
    }
  }
}

fun MovieViewModel.fetchPopularMovies() {
  if (isLoading || isLastPage) return
  isLoading = true
  viewModelScope.launch {
    try {
      val result = repository.getPopularMovies(currentPage)
      when (result) {
        is Resource.Success -> {
          val newMovies = result.data?.results ?: emptyList()
          val currentMovies =
            if (_uiState.value is MovieUiState.Success) {
              (_uiState.value as MovieUiState.Success).movies
            } else {
              emptyList()
            }
          _uiState.value = MovieUiState.Success(currentMovies + newMovies)
          currentPage++
          isLastPage = newMovies.isEmpty()
        }
        is Resource.Error -> {
          _uiState.value = MovieUiState.Error(handleError(result.message))
        }
      }
    } catch (e: Exception) {
      _uiState.value = MovieUiState.Error(handleError(e))
    } finally {
      isLoading = false
    }
  }
}

internal fun MovieViewModel.searchMovies(query: String) {
  viewModelScope.launch {
    _uiState.value = MovieUiState.Loading
    when (val result = repository.searchMovies(query, 1)) {
      is Resource.Success -> {
        _uiState.value = MovieUiState.Success(result.data?.results ?: emptyList())
      }
      is Resource.Error -> {
        _uiState.value = MovieUiState.Error(handleError(result.message))
      }
    }
  }
}
