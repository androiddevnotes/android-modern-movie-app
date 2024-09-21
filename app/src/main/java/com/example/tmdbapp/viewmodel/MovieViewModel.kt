package com.example.tmdbapp.viewmodel

import android.app.*
import androidx.lifecycle.*
import com.example.tmdbapp.data.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.utils.ApiKeyManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MovieViewModel(
  application: Application,
) : AndroidViewModel(application) {
  internal var currentPage = 1
  internal var isLoading = false
  private var searchJob: Job? = null
  internal val repository = MovieRepository(application)
  internal var isLastPage = false

  private val _currentMovie = MutableStateFlow<Movie?>(null)
  internal val _currentSortOption = MutableStateFlow(SortOption.POPULAR)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  internal val _filterOptions = MutableStateFlow(FilterOptions())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _searchQuery = MutableStateFlow("")
  private val _selectedMovie = MutableStateFlow<Movie?>(null)
  internal val _uiState = MutableStateFlow<MovieUiState>(MovieUiState.Loading)
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

  internal val apiKeyManager = ApiKeyManager(application)

  init {
    fetchPopularMovies()
    loadFavorites()
    checkAuthenticationStatus()
  }

  private val _movieDetailState = MutableStateFlow<MovieDetailState>(MovieDetailState.Loading)
  val movieDetailState: StateFlow<MovieDetailState> = _movieDetailState.asStateFlow()

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      _movieDetailState.value = MovieDetailState.Loading
      try {
        val movie = repository.getMovieDetails(movieId)
        if (movie != null) {
          _movieDetailState.value = MovieDetailState.Success(movie)
        } else {
          _movieDetailState.value = MovieDetailState.Error(MovieError.Unknown, movieId)
        }
      } catch (e: Exception) {
        _movieDetailState.value = MovieDetailState.Error(handleError(e.message, apiKeyManager), movieId)
      }
    }
  }

  fun retryFetchMovieDetails() {
    val currentState = _movieDetailState.value
    if (currentState is MovieDetailState.Error) {
      fetchMovieDetails(currentState.movieId)
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

  private val _aiResponse = MutableStateFlow<String?>(null)
  val aiResponse: StateFlow<String?> = _aiResponse.asStateFlow()

  private val _aiResponseState = MutableStateFlow<AIResponseState>(AIResponseState.Idle)
  val aiResponseState: StateFlow<AIResponseState> = _aiResponseState.asStateFlow()

  fun askAIAboutMovie(movie: Movie) {
    viewModelScope.launch {
      _aiResponseState.value = AIResponseState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      try {
        val response = repository.askOpenAI(prompt)
        _aiResponse.value = response
        _aiResponseState.value = AIResponseState.Success
      } catch (e: Exception) {
        _aiResponseState.value = AIResponseState.Error(e.localizedMessage ?: "Unknown error occurred")
      }
    }
  }

  fun clearAIResponse() {
    _aiResponse.value = null
    _aiResponseState.value = AIResponseState.Idle
  }
}

sealed class AIResponseState {
  object Idle : AIResponseState()

  object Loading : AIResponseState()

  object Success : AIResponseState()

  data class Error(
    val message: String,
  ) : AIResponseState()
}

sealed class MovieDetailState {
  object Loading : MovieDetailState()

  data class Success(
    val movie: Movie,
  ) : MovieDetailState()

  data class Error(
    val error: MovieError,
    val movieId: Int,
  ) : MovieDetailState()
}
