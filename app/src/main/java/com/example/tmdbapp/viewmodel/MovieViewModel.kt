package com.example.tmdbapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdbapp.data.SessionManager
import com.example.tmdbapp.models.Movie
import com.example.tmdbapp.repository.MovieRepository
import com.example.tmdbapp.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private var currentPage = 1
  private var isLoading = false
  private var searchJob: Job? = null
  internal val repository = MovieRepository(application)
  internal var isLastPage = false

  private val _currentMovie = MutableStateFlow<Movie?>(null)
  private val _currentSortOption = MutableStateFlow(SortOption.POPULAR)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  private val _filterOptions = MutableStateFlow(FilterOptions())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _searchQuery = MutableStateFlow("")
  private val _selectedMovie = MutableStateFlow<Movie?>(null)
  private val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
  internal val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
  internal val _createListState = MutableStateFlow<CreateListState>(CreateListState.Idle)

  val currentMovie: StateFlow<Movie?> = _currentMovie
  val currentSortOption: StateFlow<SortOption> = _currentSortOption
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val lastViewedItemIndex: StateFlow<Int> = _lastViewedItemIndex
  val searchQuery: StateFlow<String> = _searchQuery
  val uiState: StateFlow<MovieUiState> = _uiState
  val authState: StateFlow<AuthState> = _authState
  val createListState: StateFlow<CreateListState> = _createListState

  internal val sessionManager = SessionManager(application)

  init {
    fetchPopularMovies()
    loadFavorites()
    checkAuthenticationStatus()
  }

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      try {
        val movie = repository.getMovieDetails(movieId)
        _currentMovie.value = movie
      } catch (e: Exception) {
        _currentMovie.value = null
      }
    }
  }

  fun loadMoreMovies() {
    fetchMovies()
  }

  fun refreshMovies() {
    currentPage = 1
    isLastPage = false
    _uiState.value = MovieUiState.Loading
    fetchMovies()
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _uiState.value = MovieUiState.Loading
    fetchMovies()
  }

  fun setLastViewedItemIndex(index: Int) {
    _lastViewedItemIndex.value = index
  }

  fun setSearchQuery(query: String) {
    val oldQuery = _searchQuery.value
    _searchQuery.value = query
    searchJob?.cancel()
    if (query.isNotEmpty()) {
      searchJob =
        viewModelScope.launch {
          delay(Constants.DELAY_SEARCH)
          searchMovies(query)
        }
    } else if (oldQuery.isNotEmpty() && query.isEmpty()) {
      refreshMovies()
    }
  }

  fun setSortOption(sortOption: SortOption) {
    if (_currentSortOption.value != sortOption) {
      _currentSortOption.value = sortOption
      currentPage = 1
      isLastPage = false
      _uiState.value = MovieUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _currentMovie.value = updatedMovie

      when (val currentState = _uiState.value) {
        is MovieUiState.Success -> {
          val updatedMovies =
            currentState.movies.map {
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

  private fun loadFavorites() {
    viewModelScope.launch {
      val favoriteMovies = repository.getFavoriteMovies()
      _favorites.value = favoriteMovies
    }
  }

  fun isFavorite(movieId: Int): Boolean = favorites.value.any { it.id == movieId }
}
