package com.example.tmdbapp.utils

import com.example.tmdbapp.models.AlphaListUiState
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import kotlinx.coroutines.flow.MutableStateFlow

object MovieResultHandler {
  fun handleMovieResult(
    result: Resource<MovieResponse>,
    currentPage: Int,
    alphaListUiState: MutableStateFlow<AlphaListUiState<List<Movie>>>,
    apiKeyManager: ApiKeyManager,
    updateCurrentPage: (Int) -> Unit,
    updateIsLastPage: (Boolean) -> Unit,
    updateIsLoading: (Boolean) -> Unit,
  ) {
    when (result) {
      is Resource.Success -> {
        val newMovies = result.data?.results ?: emptyList()
        val currentMovies =
          if (alphaListUiState.value is AlphaListUiState.Success && currentPage > 1) {
            (alphaListUiState.value as AlphaListUiState.Success<List<Movie>>).data
          } else {
            emptyList()
          }
        alphaListUiState.value = AlphaListUiState.Success(currentMovies + newMovies)
        updateCurrentPage(currentPage + 1)
        updateIsLastPage(newMovies.isEmpty())
      }

      is Resource.Error -> {
        alphaListUiState.value = AlphaListUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
    updateIsLoading(false)
  }
}
