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
  private var searchJob: Job? = null
  private val _DetailUiState = MutableStateFlow<DetailUiState<Movie>>(DetailUiState.Loading)
  private val _aiResponseUiState =
    MutableStateFlow<AIResponseUiState<String>>(AIResponseUiState.Idle)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _scrollToIndex = MutableStateFlow<Int?>(null)
  private val _searchQuery = MutableStateFlow("")
  internal var currentPage = 1
  internal var isLastPage = false
  internal var isLoading = false
  internal val _List_uiState = MutableStateFlow<ListUiState<List<Movie>>>(ListUiState.Loading)
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
  val detailUiState: StateFlow<DetailUiState<Movie>> = _DetailUiState.asStateFlow()
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val listUiState: StateFlow<ListUiState<List<Movie>>> = _List_uiState.asStateFlow()
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

  fun isFavorite(movieId: Int): Boolean = favorites.value.any { it.id == movieId }

  fun loadMoreItems() {
    fetchMovies()
  }

  fun refreshItems() {
    currentPage = 1
    isLastPage = false
    _List_uiState.value = ListUiState.Loading
    fetchMovies()
  }

  fun retryFetchItemDetails() {
    val currentState = _DetailUiState.value
    if (currentState is DetailUiState.Error) {
      fetchMovieDetails(currentState.itemId)
    }
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
      refreshItems()
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
}
