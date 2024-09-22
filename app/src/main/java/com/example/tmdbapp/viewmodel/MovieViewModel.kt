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

  internal val _currentSortOptions = MutableStateFlow(SortOptions.POPULAR)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  internal val _filterOptions = MutableStateFlow(FilterOptions())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _searchQuery = MutableStateFlow("")
  internal val _List_uiState = MutableStateFlow<ListUiState<List<Movie>>>(ListUiState.Loading)
  internal val _authUiState = MutableStateFlow<AuthUiState<String>>(AuthUiState.Idle)
  internal val _createListUiState = MutableStateFlow<CreateListUiState<Int>>(CreateListUiState.Idle)

  val currentSortOptions: StateFlow<SortOptions> = _currentSortOptions
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val searchQuery: StateFlow<String> = _searchQuery
  val listUiState: StateFlow<ListUiState<List<Movie>>> = _List_uiState.asStateFlow()
  val authUiState: StateFlow<AuthUiState<String>> = _authUiState
  val createListUiState: StateFlow<CreateListUiState<Int>> = _createListUiState

  internal val sessionManager = SessionManager(application)

  internal val apiKeyManager = ApiKeyManager(application)

  init {
    fetchPopularMovies()
    loadFavorites()
    checkAuthenticationStatus()
  }

  private val _DetailUiState = MutableStateFlow<DetailUiState<Movie>>(DetailUiState.Loading)
  val detailUiState: StateFlow<DetailUiState<Movie>> = _DetailUiState.asStateFlow()

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      _DetailUiState.value = DetailUiState.Loading
      try {
        val movie = repository.getMovieDetails(movieId)
        if (movie != null) {
          _DetailUiState.value = DetailUiState.Success(movie)
        } else {
          _DetailUiState.value = DetailUiState.Error(AppError.Unknown, movieId)
        }
      } catch (e: Exception) {
        _DetailUiState.value = DetailUiState.Error(handleError(e.message, apiKeyManager), movieId)
      }
    }
  }

  fun retryFetchMovieDetails() {
    val currentState = _DetailUiState.value
    if (currentState is DetailUiState.Error) {
      fetchMovieDetails(currentState.movieId)
    }
  }

  fun loadMoreMovies() {
    fetchMovies()
  }

  fun refreshMovies() {
    currentPage = 1
    isLastPage = false
    _List_uiState.value = ListUiState.Loading
    fetchMovies()
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _List_uiState.value = ListUiState.Loading
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

  fun setSortOption(sortOptions: SortOptions) {
    if (_currentSortOptions.value != sortOptions) {
      _currentSortOptions.value = sortOptions
      currentPage = 1
      isLastPage = false
      _List_uiState.value = ListUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _DetailUiState.update { currentState ->
        if (currentState is DetailUiState.Success && currentState.data.id == updatedMovie.id) {
          DetailUiState.Success(updatedMovie)
        } else {
          currentState
        }
      }

      _List_uiState.update { currentState ->
        when (currentState) {
          is ListUiState.Success -> {
            val updatedMovies =
              currentState.data.map {
                if (it.id == updatedMovie.id) updatedMovie else it
              }
            ListUiState.Success(updatedMovies)
          }
          else -> currentState
        }
      }

      loadFavorites()
    }
  }

  private fun loadFavorites() {
    viewModelScope.launch {
      repository.getFavoriteMovies().collectLatest { favoriteMovies ->
        _favorites.value = favoriteMovies
      }
    }
  }

  fun isFavorite(movieId: Int): Boolean = favorites.value.any { it.id == movieId }

  private val _aiResponseUiState = MutableStateFlow<AIResponseUiState<String>>(AIResponseUiState.Idle)
  val aiResponseUiState: StateFlow<AIResponseUiState<String>> = _aiResponseUiState.asStateFlow()

  fun askAIAboutMovie(movie: Movie) {
    viewModelScope.launch {
      _aiResponseUiState.value = AIResponseUiState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      try {
        val response = repository.askOpenAI(prompt)
        _aiResponseUiState.value = AIResponseUiState.Success(response)
      } catch (e: Exception) {
        _aiResponseUiState.value = AIResponseUiState.Error(e.localizedMessage ?: "Unknown error occurred")
      }
    }
  }

  fun clearAIResponse() {
    _aiResponseUiState.value = AIResponseUiState.Idle
  }

  private val _scrollToIndex = MutableStateFlow<Int?>(null)

  fun clearScrollToIndex() {
    _scrollToIndex.value = null
  }
}
