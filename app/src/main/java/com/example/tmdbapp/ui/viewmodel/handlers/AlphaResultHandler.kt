package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.AlphaDetailUiState
import com.example.tmdbapp.models.AlphaListUiState
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.utils.Resource.Error
import com.example.tmdbapp.utils.Resource.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import timber.log.Timber

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
        val appError =
          when {
            apiKeyManager.tmdbApiKeyFlow.first().isBlank() -> AppError.ApiKeyMissing
            result.message == null -> {
              Timber.e("Error message is null")
              AppError.Unknown
            }
            else -> {
              Timber.e("Unhandled error: ${result.message}")
              AppError.ApiError(result.message)
            }
          }
        alphaListUiState.value = AlphaListUiState.Error(appError)
      }
    }
    updateIsLoading(false)
  }

  suspend fun handleAlphaDetailResult(
    result: Resource<Movie>,
    alphaDetailUiState: MutableStateFlow<AlphaDetailUiState<Movie>>,
    apiKeyManager: ApiKeyManager,
    movieId: Int,
  ) {
    when (result) {
      is Success -> {
        result.data?.let { movie ->
          alphaDetailUiState.value = AlphaDetailUiState.Success(movie)
        } ?: run {
          val appError = AppError.ApiError("No data received")
          alphaDetailUiState.value = AlphaDetailUiState.Error(appError, movieId)
        }
      }
      is Error -> {
        val appError =
          when {
            apiKeyManager.tmdbApiKeyFlow.first().isBlank() -> AppError.ApiKeyMissing
            result.message == null -> {
              Timber.e("Error message is null")
              AppError.Unknown
            }
            else -> {
              Timber.e("Unhandled error: ${result.message}")
              AppError.ApiError(result.message)
            }
          }
        alphaDetailUiState.value = AlphaDetailUiState.Error(appError, movieId)
      }
    }
  }
}
