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

  internal val _currentSortOption = MutableStateFlow(SortOption.POPULAR)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  internal val _filterOptions = MutableStateFlow(FilterOptions())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _searchQuery = MutableStateFlow("")
  internal val _uiState = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
  internal val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
  internal val _createListState = MutableStateFlow<CreateListState>(CreateListState.Idle)

  val currentSortOption: StateFlow<SortOption> = _currentSortOption
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val searchQuery: StateFlow<String> = _searchQuery
  val uiState: StateFlow<UiState<List<Movie>>> = _uiState.asStateFlow()
  val authState: StateFlow<AuthState> = _authState
  val createListState: StateFlow<CreateListState> = _createListState

  internal val sessionManager = SessionManager(application)

  internal val apiKeyManager = ApiKeyManager(application)

  init {
    fetchPopularMovies()
    loadFavorites()
    checkAuthenticationStatus()
  }

  private val _movieDetailState = MutableStateFlow<MovieDetailState<Movie>>(MovieDetailState.Loading)
  val movieDetailState: StateFlow<MovieDetailState<Movie>> = _movieDetailState.asStateFlow()

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      _movieDetailState.value = MovieDetailState.Loading
      try {
        val movie = repository.getMovieDetails(movieId)
        if (movie != null) {
          _movieDetailState.value = MovieDetailState.Success(movie)
        } else {
          _movieDetailState.value = MovieDetailState.Error(AppError.Unknown, movieId)
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
    _uiState.value = UiState.Loading
    fetchMovies()
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _uiState.value = UiState.Loading
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
      _uiState.value = UiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _movieDetailState.update { currentState ->
        if (currentState is MovieDetailState.Success && currentState.data.id == updatedMovie.id) {
          MovieDetailState.Success(updatedMovie)
        } else {
          currentState
        }
      }

      _uiState.update { currentState ->
        when (currentState) {
          is UiState.Success -> {
            val updatedMovies =
              currentState.data.map {
                if (it.id == updatedMovie.id) updatedMovie else it
              }
            UiState.Success(updatedMovies)
          }
          else -> currentState
        }
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

  private val _aiResponseState = MutableStateFlow<AIResponseState<String>>(AIResponseState.Idle)
  val aiResponseState: StateFlow<AIResponseState<String>> = _aiResponseState.asStateFlow()

  fun askAIAboutMovie(movie: Movie) {
    viewModelScope.launch {
      _aiResponseState.value = AIResponseState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      try {
        val response = repository.askOpenAI(prompt)
        _aiResponseState.value = AIResponseState.Success(response)
      } catch (e: Exception) {
        _aiResponseState.value = AIResponseState.Error(e.localizedMessage ?: "Unknown error occurred")
      }
    }
  }

  fun clearAIResponse() {
    _aiResponseState.value = AIResponseState.Idle
  }

  private val _scrollToIndex = MutableStateFlow<Int?>(null)

  fun clearScrollToIndex() {
    _scrollToIndex.value = null
  }
}
