package com.example.tmdbapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.R
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.repository.MovieRepository
import com.example.tmdbapp.utils.Resource
import com.example.tmdbapp.utils.MovieError
import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class MovieUiState {
    object Loading : MovieUiState()
    data class Success(val movies: List<Movie>) : MovieUiState()
    data class Error(val error: MovieError) : MovieUiState()
}

data class ListScrollPosition(
    val firstVisibleItemIndex: Int,
    val firstVisibleItemScrollOffset: Int
)

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository(application)

    private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
    val uiState: StateFlow<MovieUiState> = _uiState

    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie: StateFlow<Movie?> = _selectedMovie

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    private val _listScrollPosition = MutableStateFlow(ListScrollPosition(0, 0))
    val listScrollPosition: StateFlow<ListScrollPosition> = _listScrollPosition

    private val _currentMovie = MutableStateFlow<Movie?>(null)
    val currentMovie: StateFlow<Movie?> = _currentMovie

    init {
        fetchPopularMovies()
        loadFavorites()
    }

    private fun fetchPopularMovies() {
        if (isLoading || isLastPage) return
        isLoading = true
        viewModelScope.launch {
            try {
                val result = repository.getPopularMovies(currentPage)
                when (result) {
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

    private fun handleError(error: Throwable): MovieError {
        return when (error) {
            is IOException -> MovieError.Network
            is HttpException -> {
                when (error.code()) {
                    in 400..499 -> MovieError.ApiError("API Error: ${error.message()}")
                    in 500..599 -> MovieError.Server
                    else -> MovieError.Unknown
                }
            }
            else -> MovieError.Unknown
        }
    }

    private fun handleError(errorMessage: String?): MovieError {
        return MovieError.ApiError(errorMessage ?: "An unknown error occurred")
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val favoriteMovies = repository.getFavoriteMovies()
            _favorites.value = favoriteMovies
        }
    }

    fun loadMoreMovies() {
        fetchPopularMovies()
    }

    fun selectMovie(movie: Movie) {
        _selectedMovie.value = movie
        _currentMovie.value = movie
    }

    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }

    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            repository.toggleFavorite(movie)
            val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)
            
            // Update the current movie
            _currentMovie.value = updatedMovie
            
            when (val currentState = _uiState.value) {
                is MovieUiState.Success -> {
                    val updatedMovies = currentState.movies.map { 
                        if (it.id == movie.id) updatedMovie else it 
                    }
                    _uiState.value = MovieUiState.Success(updatedMovies)
                }
                else -> {} 
            }
            
            
            _selectedMovie.update { current ->
                if (current?.id == movie.id) updatedMovie else current
            }
            
            
            if (updatedMovie.isFavorite) {
                _favorites.update { it + updatedMovie }
            } else {
                _favorites.update { it.filter { m -> m.id != updatedMovie.id } }
            }
        }
    }

    fun getMovieById(movieId: Int): Movie? {
        return when (val currentState = _uiState.value) {
            is MovieUiState.Success -> currentState.movies.find { it.id == movieId }
            else -> null
        }
    }

    fun saveListScrollPosition(firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) {
        _listScrollPosition.value = ListScrollPosition(firstVisibleItemIndex, firstVisibleItemScrollOffset)
    }
}
