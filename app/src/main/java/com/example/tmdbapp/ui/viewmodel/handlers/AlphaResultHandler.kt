package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.AlphaListUiState
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import com.example.tmdbapp.utils.ApiKeyManager
import com.example.tmdbapp.utils.Resource
import com.example.tmdbapp.utils.Resource.Error
import com.example.tmdbapp.utils.Resource.Success
import kotlinx.coroutines.flow.MutableStateFlow

object AlphaResultHandler {
  suspend fun handleAlphaResult(
    result: Resource<MovieResponse>,
    currentPage: Int,
    alphaListUiState: MutableStateFlow<AlphaListUiState<List<Movie>>>,
    apiKeyManager: ApiKeyManager,
    updateCurrentPage: (Int) -> Unit,
    updateIsLastPage: (Boolean) -> Unit,
    updateIsLoading: (Boolean) -> Unit,
  ) {
    when (result) {
      is Success -> {
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

      is Error -> {
        alphaListUiState.value = AlphaListUiState.Error(handleNetworkError(result.message, apiKeyManager))
      }
    }
    updateIsLoading(false)
  }
}
