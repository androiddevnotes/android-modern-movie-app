package com.example.tmdbapp.utils

import com.example.tmdbapp.models.ItemListUiState
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import kotlinx.coroutines.flow.MutableStateFlow

object MovieResultHandler {
  fun handleMovieResult(
    result: Resource<MovieResponse>,
    currentPage: Int,
    alphaListUiState: MutableStateFlow<ItemListUiState<List<Movie>>>,
    apiKeyManager: ApiKeyManager,
    updateCurrentPage: (Int) -> Unit,
    updateIsLastPage: (Boolean) -> Unit,
    updateIsLoading: (Boolean) -> Unit,
  ) {
    when (result) {
      is Resource.Success -> {
        val newMovies = result.data?.results ?: emptyList()
        val currentMovies =
          if (alphaListUiState.value is ItemListUiState.Success && currentPage > 1) {
            (alphaListUiState.value as ItemListUiState.Success<List<Movie>>).data
          } else {
            emptyList()
          }
        alphaListUiState.value = ItemListUiState.Success(currentMovies + newMovies)
        updateCurrentPage(currentPage + 1)
        updateIsLastPage(newMovies.isEmpty())
      }

      is Resource.Error -> {
        alphaListUiState.value = ItemListUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
    updateIsLoading(false)
  }
}
