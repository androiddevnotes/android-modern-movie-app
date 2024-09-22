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

class ItemViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private var searchJob: Job? = null
  private val _detailUiState = MutableStateFlow<DetailUiState<Movie>>(DetailUiState.Loading)
  private val _aiResponseUiState =
    MutableStateFlow<AIResponseUiState<String>>(AIResponseUiState.Idle)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _scrollToIndex = MutableStateFlow<Int?>(null)
  private val _searchQuery = MutableStateFlow("")
  internal var currentPage = 1
  internal var isLastPage = false
  internal var isLoading = false
  internal val _listUiState = MutableStateFlow<ListUiState<List<Movie>>>(ListUiState.Loading)
  internal val _authUiState = MutableStateFlow<AuthUiState<String>>(AuthUiState.Idle)
  internal val _createListUiState = MutableStateFlow<CreateListUiState<Int>>(CreateListUiState.Idle)
  internal val _currentSortOptions = MutableStateFlow(SortOptions.POPULAR)
  internal val _filterOptions = MutableStateFlow(FilterOptions())
  internal val apiKeyManager = ApiKeyManager(application)
  internal val repository = ItemRepository(application)
  internal val sessionManager = SessionManager(application)
  val aiResponseUiState: StateFlow<AIResponseUiState<String>> = _aiResponseUiState.asStateFlow()
  val authUiState: StateFlow<AuthUiState<String>> = _authUiState
  val createListUiState: StateFlow<CreateListUiState<Int>> = _createListUiState
  val currentSortOptions: StateFlow<SortOptions> = _currentSortOptions
  val detailUiState: StateFlow<DetailUiState<Movie>> = _detailUiState.asStateFlow()
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val listUiState: StateFlow<ListUiState<List<Movie>>> = _listUiState.asStateFlow()
  val searchQuery: StateFlow<String> = _searchQuery

  init {
    fetchPopularMovies()
    loadFavorites()
    checkAuthenticationStatus()
  }

  fun askAIAboutItem(movie: Movie) {
    viewModelScope.launch {
      _aiResponseUiState.value = AIResponseUiState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      try {
        val response = repository.askOpenAI(prompt)
        _aiResponseUiState.value = AIResponseUiState.Success(response)
      } catch (e: Exception) {
        _aiResponseUiState.value =
          AIResponseUiState.Error(e.localizedMessage ?: "Unknown error occurred")
      }
    }
  }

  fun clearAIResponse() {
    _aiResponseUiState.value = AIResponseUiState.Idle
  }

  fun clearScrollToIndex() {
    _scrollToIndex.value = null
  }

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      _detailUiState.value = DetailUiState.Loading
      try {
        val movie = repository.getMovieDetails(movieId)
        if (movie != null) {
          _detailUiState.value = DetailUiState.Success(movie)
        } else {
          _detailUiState.value = DetailUiState.Error(AppError.Unknown, movieId)
        }
      } catch (e: Exception) {
        _detailUiState.value = DetailUiState.Error(handleNetworkError(e.message, apiKeyManager), movieId)
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
    _listUiState.value = ListUiState.Loading
    fetchMovies()
  }

  fun retryFetchItemDetails() {
    val currentState = _detailUiState.value
    if (currentState is DetailUiState.Error) {
      fetchMovieDetails(currentState.itemId)
    }
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _listUiState.value = ListUiState.Loading
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
      _listUiState.value = ListUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _detailUiState.update { currentState ->
        if (currentState is DetailUiState.Success && currentState.data.id == updatedMovie.id) {
          DetailUiState.Success(updatedMovie)
        } else {
          currentState
        }
      }

      _listUiState.update { currentState ->
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
}
