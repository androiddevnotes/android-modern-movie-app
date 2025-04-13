package com.example.tmdbapp.ui.viewmodel

import android.app.*
import androidx.lifecycle.*
import com.example.tmdbapp.data.*
import com.example.tmdbapp.models.*
import com.example.tmdbapp.repository.*
import com.example.tmdbapp.ui.viewmodel.handlers.LlmResultHandler
import com.example.tmdbapp.utils.*
import com.example.tmdbapp.utils.ApiKeyManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { 
        TmdbViewModel(
            application = get(),
            repository = get(),
            apiKeyManager = get(),
            sessionManagerPreferencesDataStore = get()
        )
    }
}

class TmdbViewModel(
  application: Application,
  internal val repository: Repository,
  internal val apiKeyManager: ApiKeyManager,
  internal val sessionManagerPreferencesDataStore: SessionManagerPreferencesDataStore,
) : AndroidViewModel(application) {
  private var searchJob: Job? = null

  private val _betaPieceUiState =
    MutableStateFlow<BetaPieceUiState<String>>(BetaPieceUiState.Idle)

  internal val _tmdbDetailUiState =
    MutableStateFlow<TmdbDetailUiState<Movie>>(TmdbDetailUiState.Loading)

  private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
  val favorites: StateFlow<List<Movie>> = _favorites.asStateFlow()
  private val _lastViewedItemIndex = MutableStateFlow(0)
  private val _scrollToIndex = MutableStateFlow<Int?>(null)
  private val _searchQuery = MutableStateFlow("")
  internal var currentPage = 1
  internal var isLastPage = false
  internal var isLoading = false
  internal val _tmdbAuthUiState = MutableStateFlow<TmdbAuthUiState<String>>(TmdbAuthUiState.Idle)

  internal val _tmdbCreateListUiState =
    MutableStateFlow<TmdbCreateListUiState<Int>>(TmdbCreateListUiState.Idle)

  internal val _tmdbListUiState =
    MutableStateFlow<TmdbListUiState<List<Movie>>>(TmdbListUiState.Loading)

  internal val _currentSortOptions = MutableStateFlow(SortOptions.POPULAR)
  internal val _filterOptions = MutableStateFlow(FilterOptions())

  val betaPieceUiState: StateFlow<BetaPieceUiState<String>> = _betaPieceUiState.asStateFlow()
  val currentSortOptions: StateFlow<SortOptions> = _currentSortOptions
  val filterOptions: StateFlow<FilterOptions> = _filterOptions
  val tmdbAuthUiState: StateFlow<TmdbAuthUiState<String>> = _tmdbAuthUiState
  val tmdbCreateListUiState: StateFlow<TmdbCreateListUiState<Int>> = _tmdbCreateListUiState
  val tmdbDetailUiState: StateFlow<TmdbDetailUiState<Movie>> = _tmdbDetailUiState.asStateFlow()
  val tmdbListUiState: StateFlow<TmdbListUiState<List<Movie>>> = _tmdbListUiState.asStateFlow()
  val searchQuery: StateFlow<String> = _searchQuery

  init {
    fetchMovies() // This will fetch popular movies by default
    loadFavorites()
    checkAuthenticationStatus()
  }

  fun askLlmAboutItem(movie: Movie) {
    viewModelScope.launch {
      _betaPieceUiState.value = BetaPieceUiState.Loading
      val prompt = "Tell me about the movie '${movie.title}' in a brief paragraph."
      val result = repository.askOpenAi(prompt)
      LlmResultHandler.handleLlmResult(
        result = result,
        betaPieceUiState = _betaPieceUiState,
        apiKeyManager = apiKeyManager,
      )
    }
  }

  fun clearAIResponse() {
    _betaPieceUiState.value = BetaPieceUiState.Idle
  }

  fun clearScrollToIndex() {
    _scrollToIndex.value = null
  }

  fun isFavorite(movieId: Int): Boolean = favorites.value.any { it.id == movieId }

  fun loadMoreItems() {
    fetchMovies()
  }

  fun refreshItems() {
    currentPage = 1
    isLastPage = false
    _tmdbListUiState.value = TmdbListUiState.Loading
    fetchMovies()
  }

  fun retryFetchItemDetails() {
    val currentState = _tmdbDetailUiState.value
    if (currentState is TmdbDetailUiState.Error) {
      fetchMovieDetails(currentState.itemId)
    }
  }

  fun setFilterOptions(options: FilterOptions) {
    _filterOptions.value = options
    currentPage = 1
    isLastPage = false
    _tmdbListUiState.value = TmdbListUiState.Loading
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
      _tmdbListUiState.value = TmdbListUiState.Loading
      fetchMovies()
    }
  }

  fun toggleFavorite(movie: Movie) {
    viewModelScope.launch {
      repository.toggleFavorite(movie)
      val updatedMovie = movie.copy(isFavorite = !movie.isFavorite)

      _tmdbDetailUiState.update { currentState ->
        if (currentState is TmdbDetailUiState.Success && currentState.data.id == updatedMovie.id) {
          TmdbDetailUiState.Success(updatedMovie)
        } else {
          currentState
        }
      }

      _tmdbListUiState.update { currentState ->
        when (currentState) {
          is TmdbListUiState.Success -> {
            val updatedMovies =
              currentState.data.map {
                if (it.id == updatedMovie.id) updatedMovie else it
              }
            TmdbListUiState.Success(updatedMovies)
          }
          else -> currentState
        }
      }

      loadFavorites()
    }
  }

  fun loadFavorites() {
    viewModelScope.launch {
      repository.getFavoriteMovies().collectLatest { favoriteMovies ->
        _favorites.value = favoriteMovies
      }
    }
  }
}
