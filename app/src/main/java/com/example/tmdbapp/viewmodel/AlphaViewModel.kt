package com.example.tmdbapp.viewmodel

import android.app.*
import androidx.lifecycle.*
import com.example.tmdbapp.data.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.network.handleNetworkError
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.utils.ApiKeyManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class AlphaViewModel(
  application: Application,
) : AndroidViewModel(application) {
  private var searchJob: Job? = null
  private val _Item_detailUiState = MutableStateFlow<ItemDetailUiState<Movie>>(ItemDetailUiState.Loading)
  private val _aiResponseUiState =
    MutableStateFlow<AiResponseUiState<String>>(AiResponseUiState.Idle)
  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _scrollToIndex = MutableStateFlow<Int?>(null)
  private val _searchQuery = MutableStateFlow("")
  internal var currentPage = 1
  internal var isLastPage = false
  internal var isLoading = false
  internal val _Item_listUiState = MutableStateFlow<ItemListUiState<List<Movie>>>(ItemListUiState.Loading)
  internal val _Item_authUiState = MutableStateFlow<ItemAuthUiState<String>>(ItemAuthUiState.Idle)
  internal val _Item_createListUiState = MutableStateFlow<ItemCreateListUiState<Int>>(ItemCreateListUiState.Idle)
  internal val _currentSortOptions = MutableStateFlow(SortOptions.POPULAR)
  internal val _filterOptions = MutableStateFlow(FilterOptions())
  internal val apiKeyManager = ApiKeyManager(application)
  internal val repository = Repository(application)
  internal val sessionManagerPreferencesDataStore = SessionManagerPreferencesDataStore(application)
  val aiResponseUiState: StateFlow<AiResponseUiState<String>> = _aiResponseUiState.asStateFlow()
  val itemAuthUiState: StateFlow<ItemAuthUiState<String>> = _Item_authUiState
  val itemCreateListUiState: StateFlow<ItemCreateListUiState<Int>> = _Item_createListUiState
  val currentSortOptions: StateFlow<SortOptions> = _currentSortOptions
  val itemDetailUiState: StateFlow<ItemDetailUiState<Movie>> = _Item_detailUiState.asStateFlow()
  val favorites: StateFlow<List<Movie>> = _favorites
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val itemListUiState: StateFlow<ItemListUiState<List<Movie>>> = _Item_listUiState.asStateFlow()
  val searchQuery: StateFlow<String> = _searchQuery

  init {
    fetchPopularMovies()
    loadFavorites()
    checkAuthenticationStatus()
  }

  fun askAIAboutItem(movie: Movie) {
    viewModelScope.launch {
      _aiResponseUiState.value = AiResponseUiState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      try {
        val response = repository.askOpenAi(prompt)
        _aiResponseUiState.value = AiResponseUiState.Success(response)
      } catch (e: Exception) {
        _aiResponseUiState.value =
          AiResponseUiState.Error(e.localizedMessage ?: "Unknown error occurred")
      }
    }
  }

  fun clearAIResponse() {
    _aiResponseUiState.value = AiResponseUiState.Idle
  }

  fun clearScrollToIndex() {
    _scrollToIndex.value = null
  }

  fun fetchMovieDetails(movieId: Int) {
    viewModelScope.launch {
      _Item_detailUiState.value = ItemDetailUiState.Loading
      try {
        val movie = repository.getMovieDetails(movieId)
        if (movie != null) {
          _Item_detailUiState.value = ItemDetailUiState.Success(movie)
        } else {
          _Item_detailUiState.value = ItemDetailUiState.Error(AppError.Unknown, movieId)
        }
      } catch (e: Exception) {
        _Item_detailUiState.value = ItemDetailUiState.Error(handleNetworkError(e.message, apiKeyManager), movieId)
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
    _Item_listUiState.value = ItemListUiState.Loading
    fetchMovies()
  }

  fun retryFetchItemDetails() {
    val currentState = _Item_detailUiState.value
    if (currentState is ItemDetailUiState.Error) {
      fetchMovieDetails(currentState.itemId)
    }
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _Item_listUiState.value = ItemListUiState.Loading
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
      _Item_listUiState.value = ItemListUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _Item_detailUiState.update { currentState ->
        if (currentState is ItemDetailUiState.Success && currentState.data.id == updatedMovie.id) {
          ItemDetailUiState.Success(updatedMovie)
        } else {
          currentState
        }
      }

      _Item_listUiState.update { currentState ->
        when (currentState) {
          is ItemListUiState.Success -> {
            val updatedMovies =
              currentState.data.map {
                if (it.id == updatedMovie.id) updatedMovie else it
              }
            ItemListUiState.Success(updatedMovies)
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
