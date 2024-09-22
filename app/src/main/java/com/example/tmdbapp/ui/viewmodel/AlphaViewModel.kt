package com.example.tmdbapp.ui.viewmodel

import android.app.*
import androidx.lifecycle.*
import com.example.tmdbapp.data.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.ui.viewmodel.handlers.BetaResultHandler
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.utils.ApiKeyManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AlphaViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private var searchJob: Job? = null

  private val _betaResponseUiState =
    MutableStateFlow<BetaResponseUiState<String>>(BetaResponseUiState.Idle)

  private val _alphaDetailUiState =
    MutableStateFlow<AlphaDetailUiState<Movie>>(AlphaDetailUiState.Loading)

  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _scrollToIndex = MutableStateFlow<Int?>(null)
  private val _searchQuery = MutableStateFlow("")
  internal var currentPage = 1
  internal var isLastPage = false
  internal var isLoading = false
  internal val _alphaAuthUiState = MutableStateFlow<AlphaAuthUiState<String>>(AlphaAuthUiState.Idle)

  internal val _alphaCreateListUiState =
    MutableStateFlow<AlphaCreateListUiState<Int>>(AlphaCreateListUiState.Idle)

  internal val _alphaListUiState =
    MutableStateFlow<AlphaListUiState<List<Movie>>>(AlphaListUiState.Loading)

  internal val _currentSortOptions = MutableStateFlow(SortOptions.POPULAR)
  internal val _filterOptions = MutableStateFlow(FilterOptions())
  internal val apiKeyManager = ApiKeyManager(application)
  internal val repository = Repository(application)
  internal val sessionManagerPreferencesDataStore = SessionManagerPreferencesDataStore(application)
  val betaResponseUiState: StateFlow<BetaResponseUiState<String>> = _betaResponseUiState.asStateFlow()
  val currentSortOptions: StateFlow<SortOptions> = _currentSortOptions
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val alphaAuthUiState: StateFlow<AlphaAuthUiState<String>> = _alphaAuthUiState
  val alphaCreateListUiState: StateFlow<AlphaCreateListUiState<Int>> = _alphaCreateListUiState
  val alphaDetailUiState: StateFlow<AlphaDetailUiState<Movie>> = _alphaDetailUiState.asStateFlow()
  val alphaListUiState: StateFlow<AlphaListUiState<List<Movie>>> = _alphaListUiState.asStateFlow()
  val searchQuery: StateFlow<String> = _searchQuery

  init {
    fetchMovies() // This will fetch popular movies by default
    loadFavorites()
    checkAuthenticationStatus()
  }

  fun askAIAboutItem(movie: Movie) {
    viewModelScope.launch {
      _betaResponseUiState.value = BetaResponseUiState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      try {
        val result = repository.askOpenAi(prompt)
        BetaResultHandler.handleBetaResult(
          result,
          _betaResponseUiState,
          apiKeyManager,
        )
      } catch (e: Exception) {
        _betaResponseUiState.value =
          BetaResponseUiState.Error(
            handleNetworkError(e.localizedMessage, apiKeyManager),
          )
      }
    }
  }

  fun clearAIResponse() {
    _betaResponseUiState.value = BetaResponseUiState.Idle
  }

  fun clearScrollToIndex() {
    _scrollToIndex.value = null
  }

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      _alphaDetailUiState.value = AlphaDetailUiState.Loading
      try {
        val movie = repository.getMovieDetails(movieId)
        if (movie != null) {
          _alphaDetailUiState.value = AlphaDetailUiState.Success(movie)
        } else {
          _alphaDetailUiState.value = AlphaDetailUiState.Error(AppError.Unknown, movieId)
        }
      } catch (e: Exception) {
        _alphaDetailUiState.value =
          AlphaDetailUiState.Error(handleNetworkError(e.message, apiKeyManager), movieId)
      }
    }
  }

  fun isFavorite(movieId: Int): Boolean = favorites.value.any { it.id == movieId }

  fun loadMoreItems() {
    fetchMovies()
  }

  fun refreshItems() {
    currentPage = 1
    isLastPage = false
    _alphaListUiState.value = AlphaListUiState.Loading
    fetchMovies()
  }

  fun retryFetchItemDetails() {
    val currentState = _alphaDetailUiState.value
    if (currentState is AlphaDetailUiState.Error) {
      fetchMovieDetails(currentState.itemId)
    }
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _alphaListUiState.value = AlphaListUiState.Loading
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
      refreshItems()
    }
  }

  fun setSortOption(sortOptions: SortOptions) {
    if (_currentSortOptions.value != sortOptions) {
      _currentSortOptions.value = sortOptions
      currentPage = 1
      isLastPage = false
      _alphaListUiState.value = AlphaListUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _alphaDetailUiState.update { currentState ->
        if (currentState is AlphaDetailUiState.Success && currentState.data.id == updatedMovie.id) {
          AlphaDetailUiState.Success(updatedMovie)
        } else {
          currentState
        }
      }

      _alphaListUiState.update { currentState ->
        when (currentState) {
          is AlphaListUiState.Success -> {
            val updatedMovies =
              currentState.data.map {
                if (it.id == updatedMovie.id) updatedMovie else it
              }
            AlphaListUiState.Success(updatedMovies)
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
}
