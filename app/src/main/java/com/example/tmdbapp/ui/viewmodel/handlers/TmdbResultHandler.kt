package com.example.tmdbapp.ui.viewmodel.handlers

import com.example.tmdbapp.models.TmdbDetailUiState
import com.example.tmdbapp.models.TmdbListUiState
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.network.responses.tmdb.MovieResponse
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.utils.Resource.Error
import com.example.tmdbapp.utils.Resource.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import timber.log.Timber

object TmdbResultHandler {
  suspend fun handleTmdbResult(
    result: Resource<MovieResponse>,
    currentPage: Int,
    tmdbListUiState: MutableStateFlow<TmdbListUiState<List<Movie>>>,
    apiKeyManager: ApiKeyManager,
    updateCurrentPage: (Int) -> Unit,
    updateIsLastPage: (Boolean) -> Unit,
    updateIsLoading: (Boolean) -> Unit,
  ) {
    when (result) {
      is Success -> {
        val newMovies = result.data?.results ?: emptyList()
        val currentMovies =
          if (tmdbListUiState.value is TmdbListUiState.Success && currentPage > 1) {
            (tmdbListUiState.value as TmdbListUiState.Success<List<Movie>>).data
          } else {
            emptyList()
          }
        tmdbListUiState.value = TmdbListUiState.Success(currentMovies + newMovies)
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
        tmdbListUiState.value = TmdbListUiState.Error(appError)
      }
    }
    updateIsLoading(false)
  }

  suspend fun handleTmdbDetailResult(
      result: Resource<Movie>,
      tmdbDetailUiState: MutableStateFlow<TmdbDetailUiState<Movie>>,
      apiKeyManager: ApiKeyManager,
      movieId: Int,
  ) {
    when (result) {
      is Success -> {
        result.data?.let { movie ->
          tmdbDetailUiState.value = TmdbDetailUiState.Success(movie)
        } ?: run {
          val appError = AppError.ApiError("No data received")
          tmdbDetailUiState.value = TmdbDetailUiState.Error(appError, movieId)
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
        tmdbDetailUiState.value = TmdbDetailUiState.Error(appError, movieId)
      }
    }
  }
}
