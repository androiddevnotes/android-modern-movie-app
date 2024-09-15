package com.example.tmdbapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.repository.MovieRepository
import com.example.tmdbapp.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MovieUiState {
    object Loading : MovieUiState()
    data class Success(val movies: List<Movie>) : MovieUiState()
    data class Error(val message: String) : MovieUiState()
}

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    init {
        fetchPopularMovies()
    }

    private fun fetchPopularMovies() {
        if (isLoading || isLastPage) return
        isLoading = true
        viewModelScope.launch {
            when (val result = repository.getPopularMovies(currentPage)) {
                is Resource.Success -> {
                    val newMovies = result.data?.results ?: emptyList()
                    val currentMovies = if (_uiState.value is MovieUiState.Success) {
                        (_uiState.value as MovieUiState.Success).movies
                    } else {
                        emptyList()
                    }
                    _uiState.value = MovieUiState.Success(currentMovies + newMovies)
                    currentPage++
                    isLastPage = newMovies.isEmpty()
                    isLoading = false
                }
                is Resource.Error -> {
                    _uiState.value = MovieUiState.Error(result.message ?: "Unknown error")
                    isLoading = false
                }
            }
        }
    }

    fun loadMoreMovies() {
        fetchPopularMovies()
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
    }

    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }
}
